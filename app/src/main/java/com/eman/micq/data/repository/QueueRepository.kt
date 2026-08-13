package com.eman.micq.data.repository

import com.eman.micq.data.model.QueueEntry
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface QueueRepository {
    fun getQueueForSession(sessionId: String): Flow<List<QueueEntry>>
    suspend fun addToQueue(sessionId: String, entry: QueueEntry): Result<Unit>
    suspend fun removeFromQueue(sessionId: String, entryId: String): Result<Unit>
    suspend fun updateEntryStatus(sessionId: String, entryId: String, status: String): Result<Unit>
}

@Singleton
class QueueRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase
) : QueueRepository {

    override fun getQueueForSession(sessionId: String): Flow<List<QueueEntry>> = callbackFlow {
        val queueRef = database.getReference("sessions/$sessionId/queue")
        
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val entries = snapshot.children.mapNotNull { it.getValue(QueueEntry::class.java) }
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

    override suspend fun addToQueue(sessionId: String, entry: QueueEntry): Result<Unit> {
        return try {
            val queueRef = database.getReference("sessions/$sessionId/queue").push()
            val newEntry = entry.copy(id = queueRef.key ?: "")
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

    override suspend fun updateEntryStatus(sessionId: String, entryId: String, status: String): Result<Unit> {
        return try {
            database.getReference("sessions/$sessionId/queue/$entryId/status").setValue(status).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
