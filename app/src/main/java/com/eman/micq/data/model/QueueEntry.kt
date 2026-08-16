package com.eman.micq.data.model

data class QueueEntry(
    val id: String = "",
    val sessionId: String = "",
    val performerId: String = "",
    val djId: String = "",
    val djName: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val preferredName: String = "",
    val songName: String = "",
    val tableNumber: String = "",
    val status: String = "WAITING", // WAITING, SINGING, DONE
    val timestamp: Long = System.currentTimeMillis(),
    val completedAt: Long? = null,
    val isKaraoke: Boolean = false
)
