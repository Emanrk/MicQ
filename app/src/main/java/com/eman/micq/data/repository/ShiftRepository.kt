package com.eman.micq.data.repository

import com.eman.micq.data.model.DjShift
import com.eman.micq.data.model.ShiftEvent
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ServerValue
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface ShiftRepository {
    suspend fun startShift(djId: String, djName: String): Result<DjShift>
    suspend fun endShift(shiftId: String): Result<Unit>
    suspend fun getActiveShift(djId: String): Result<DjShift?>
    suspend fun getAllShifts(): Result<List<DjShift>>
    suspend fun updateLastActive(shiftId: String): Result<Unit>
    suspend fun logShiftEvent(shiftId: String, eventType: String): Result<Unit>
    fun trackPresence(shiftId: String)
}

@Singleton
class ShiftRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase
) : ShiftRepository {

    override suspend fun startShift(djId: String, djName: String): Result<DjShift> {
        return try {
            val shiftRef = database.getReference("shifts").push()
            val shift = DjShift(
                id = shiftRef.key ?: "",
                djId = djId,
                djName = djName,
                startTime = System.currentTimeMillis()
            )
            shiftRef.setValue(shift).await()
            Result.success(shift)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun endShift(shiftId: String): Result<Unit> {
        return try {
            database.getReference("shifts/$shiftId/endTime")
                .setValue(System.currentTimeMillis()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getActiveShift(djId: String): Result<DjShift?> {
        return try {
            val snapshot = database.getReference("shifts")
                .orderByChild("djId").equalTo(djId).get().await()
            
            val activeShift = snapshot.children.mapNotNull { it.getValue(DjShift::class.java) }
                .find { it.endTime == null }
            
            Result.success(activeShift)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getAllShifts(): Result<List<DjShift>> {
        return try {
            val snapshot = database.getReference("shifts").get().await()
            val shifts = snapshot.children.mapNotNull { it.getValue(DjShift::class.java) }
            Result.success(shifts.sortedByDescending { it.startTime })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateLastActive(shiftId: String): Result<Unit> {
        return try {
            database.getReference("shifts/$shiftId/lastActiveAt")
                .setValue(ServerValue.TIMESTAMP).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun logShiftEvent(shiftId: String, eventType: String): Result<Unit> {
        return try {
            val eventRef = database.getReference("shifts/$shiftId/events").push()
            val event = ShiftEvent(type = eventType, timestamp = System.currentTimeMillis())
            eventRef.setValue(event).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun trackPresence(shiftId: String) {
        val connectedRef = database.getReference(".info/connected")
        val lastActiveRef = database.getReference("shifts/$shiftId/lastActiveAt")
        val eventsRef = database.getReference("shifts/$shiftId/events")

        connectedRef.addValueEventListener(object : com.google.firebase.database.ValueEventListener {
            override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false
                if (connected) {
                    // Reconnected
                    val eventRef = eventsRef.push()
                    eventRef.setValue(ShiftEvent(type = "RECONNECT", timestamp = System.currentTimeMillis()))
                    
                    // Set up onDisconnect for lastActiveAt
                    lastActiveRef.onDisconnect().setValue(ServerValue.TIMESTAMP)
                }
            }

            override fun onCancelled(error: com.google.firebase.database.DatabaseError) {}
        })
    }
}
