package com.eman.micq.data.model

data class QueueEntry(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val songName: String = "",
    val tableNumber: String = "",
    val status: String = "WAITING", // WAITING, SINGING, DONE
    val timestamp: Long = System.currentTimeMillis()
)
