package com.eman.micq.data.model

data class QueueItem(
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
    val status: String = STATUS_WAITING, // WAITING, NEXT, SINGING, DONE
    val timestamp: Long = 0L,
    val completedAt: Long? = null,
    val isKaraoke: Boolean = false
) {
    companion object {
        const val STATUS_WAITING = "WAITING"
        const val STATUS_NEXT = "NEXT"
        const val STATUS_SINGING = "SINGING"
        const val STATUS_DONE = "DONE"
    }
}
