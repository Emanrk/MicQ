package com.eman.micq.data.repository

import com.eman.micq.data.model.DjShift
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface ShiftRepository {
    suspend fun startShift(djId: String, djName: String): Result<DjShift>
    suspend fun endShift(shiftId: String): Result<Unit>
    suspend fun getActiveShift(djId: String): Result<DjShift?>
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
}
