package com.eman.micq.data.repository

import com.eman.micq.data.model.QueueItem
import com.eman.micq.data.model.Session
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.MutableData
import com.google.firebase.database.Transaction
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

data class SingerLoyalty(
    val firstName: String,
    val lastName: String,
    val preferredName: String,
    val visitCount: Int,
    val lastVisitTimestamp: Long
)

interface QueueRepository {
    fun getQueueForSession(sessionId: String): Flow<List<QueueItem>>
    suspend fun addToQueue(sessionId: String, entry: QueueItem): Result<Unit>
    suspend fun removeFromQueue(sessionId: String, entryId: String): Result<Unit>
    suspend fun updateEntryStatus(
        sessionId: String,
        entryId: String,
        status: String,
        djId: String? = null,
        djName: String? = null
    ): Result<Unit>
    suspend fun setExclusiveNext(sessionId: String, entryId: String): Result<Unit>
    suspend fun getCompletedEntries(sessionId: String): Result<List<QueueItem>>
    suspend fun getUserSongHistory(userId: String, role: String, sinceTimestamp: Long): Result<List<QueueItem>>
    suspend fun getSongCountForDj(djId: String, startTime: Long, endTime: Long): Result<Int>
    suspend fun getLoyaltyData(): Result<List<SingerLoyalty>>
}

@Singleton
class QueueRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase
) : QueueRepository {

    override fun getQueueForSession(sessionId: String): Flow<List<QueueItem>> = callbackFlow {
        val queueRef = database.getReference("sessions/$sessionId/queue")
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val entries = snapshot.children.mapNotNull { it.getValue(QueueItem::class.java) }
                    .sortedBy { it.timestamp }
                trySend(entries)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        queueRef.addValueEventListener(listener)

        awaitClose {
            queueRef.removeEventListener(listener)
        }
    }

    override suspend fun addToQueue(sessionId: String, entry: QueueItem): Result<Unit> {
        return try {
            val sessionSnapshot = database.getReference("sessions/$sessionId").get().await()
            val isKaraoke = sessionSnapshot.child("isKaraoke").getValue(Boolean::class.java) ?: false
            
            val queueRef = database.getReference("sessions/$sessionId/queue").push()
            val newEntry = entry.copy(id = queueRef.key ?: "", isKaraoke = isKaraoke)
            queueRef.setValue(newEntry).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFromQueue(sessionId: String, entryId: String): Result<Unit> {
        return try {
            database.getReference("sessions/$sessionId/queue/$entryId").removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateEntryStatus(
        sessionId: String,
        entryId: String,
        status: String,
        djId: String?,
        djName: String?
    ): Result<Unit> {
        return try {
            val entryRef = database.getReference("sessions/$sessionId/queue/$entryId")
            val snapshot = entryRef.get().await()
            val entry = snapshot.getValue(QueueItem::class.java) ?: throw Exception("Entry not found")

            val sessionUpdates = mutableMapOf<String, Any>(
                "status" to status
            )
            
            if (status == "DONE") {
                val completedAt = System.currentTimeMillis()
                sessionUpdates["completedAt"] = completedAt
                if (djId != null) sessionUpdates["djId"] = djId
                if (djName != null) sessionUpdates["djName"] = djName
                
                // Prepare atomic dual-write for history
                val historyEntry = entry.copy(
                    status = "DONE",
                    completedAt = completedAt,
                    djId = djId ?: "",
                    djName = djName ?: ""
                )
                
                val rootUpdates = mutableMapOf<String, Any>()
                // Update session queue
                sessionUpdates.forEach { (key, value) ->
                    rootUpdates["sessions/$sessionId/queue/$entryId/$key"] = value
                }
                
                // Add to DJ history
                if (djId != null) {
                    rootUpdates["history/by_dj/$djId/$entryId"] = historyEntry
                }
                
                // Add to Performer history (the staff who added the entry)
                if (entry.performerId.isNotEmpty()) {
                    rootUpdates["history/by_performer/${entry.performerId}/$entryId"] = historyEntry
                }

                // Add to flat karaoke history if applicable
                if (entry.isKaraoke) {
                    rootUpdates["history/karaoke/$entryId"] = historyEntry
                }
                
                database.getReference().updateChildren(rootUpdates).await()
            } else {
                entryRef.updateChildren(sessionUpdates).await()
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun setExclusiveNext(
        sessionId: String,
        entryId: String
    ): Result<Unit> = suspendCancellableCoroutine { continuation ->

        val queueRef = database.getReference(
            "sessions/$sessionId/queue"
        )

        queueRef.runTransaction(object : Transaction.Handler {

            override fun doTransaction(
                currentData: MutableData
            ): Transaction.Result {

                var targetFound = false

                currentData.children.forEach { child ->
                    val id = child.key
                    val currentStatus =
                        child.child("status").getValue(String::class.java)

                    if (id == entryId) {
                        child.child("status").value =
                            QueueItem.STATUS_NEXT

                        targetFound = true

                    } else if (currentStatus == QueueItem.STATUS_NEXT) {
                        child.child("status").value =
                            QueueItem.STATUS_WAITING
                    }
                }

                return if (targetFound) {
                    Transaction.success(currentData)
                } else {
                    Transaction.abort()
                }
            }

            override fun onComplete(
                error: DatabaseError?,
                committed: Boolean,
                snapshot: DataSnapshot?
            ) {
                if (continuation.isActive) {
                    when {
                        error != null -> {
                            continuation.resume(
                                Result.failure(error.toException())
                            )
                        }

                        !committed -> {
                            continuation.resume(
                                Result.failure(
                                    Exception(
                                        "Entry $entryId not found in queue"
                                    )
                                )
                            )
                        }

                        else -> {
                            continuation.resume(Result.success(Unit))
                        }
                    }
                }
            }
        })
    }

    override suspend fun getCompletedEntries(sessionId: String): Result<List<QueueItem>> {
        return try {
            val snapshot = database.getReference("sessions/$sessionId/queue")
                .orderByChild("status").equalTo("DONE").get().await()
            val entries = snapshot.children.mapNotNull { it.getValue(QueueItem::class.java) }
            Result.success(entries.sortedByDescending { it.completedAt })
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getUserSongHistory(userId: String, role: String, sinceTimestamp: Long): Result<List<QueueItem>> {
        return try {
            val path = if (role == "DJ") "history/by_dj/$userId" else "history/by_performer/$userId"
            val snapshot = database.getReference(path)
                .orderByChild("completedAt")
                .startAt(sinceTimestamp.toDouble())
                .get().await()
            
            val entries = snapshot.children.mapNotNull { it.getValue(QueueItem::class.java) }
                .sortedByDescending { it.completedAt }
            
            Result.success(entries)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getSongCountForDj(djId: String, startTime: Long, endTime: Long): Result<Int> {
        return try {
            val snapshot = database.getReference("history/by_dj/$djId")
                .orderByChild("completedAt")
                .startAt(startTime.toDouble())
                .endAt(endTime.toDouble())
                .get().await()
            
            Result.success(snapshot.childrenCount.toInt())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getLoyaltyData(): Result<List<SingerLoyalty>> {
        return try {
            val snapshot = database.getReference("history/karaoke").get().await()
            val allEntries = snapshot.children.mapNotNull { it.getValue(QueueItem::class.java) }
            
            val loyaltyMap = mutableMapOf<String, MutableList<QueueItem>>()
            
            allEntries.forEach { entry ->
                // Normalize names for matching
                val key = "${entry.firstName.lowercase().trim()}|${entry.lastName.lowercase().trim()}|${entry.preferredName.lowercase().trim()}"
                loyaltyMap.getOrPut(key) { mutableListOf() }.add(entry)
            }
            
            val loyaltyList = loyaltyMap.values.map { entries ->
                val first = entries.first()
                SingerLoyalty(
                    firstName = first.firstName,
                    lastName = first.lastName,
                    preferredName = first.preferredName,
                    visitCount = entries.map { it.sessionId }.distinct().size,
                    lastVisitTimestamp = entries.mapNotNull { it.completedAt }.maxOrNull() ?: first.timestamp
                )
            }.sortedByDescending { it.visitCount }
            
            Result.success(loyaltyList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
