package com.eman.micq.data.model

data class QueueItem(
    var id: String = "",
    var sessionId: String = "",
    var performerId: String = "",
    var djId: String = "",
    var djName: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var preferredName: String = "",
    var songName: String = "",
    var tableNumber: String = "",
    var status: String = "WAITING", // WAITING, SINGING, DONE
    var timestamp: Long = 0L,
    var completedAt: Long? = null,
    var isKaraoke: Boolean = false
) {
    constructor() : this("", "", "", "", "", "", "", "", "", "", "WAITING", 0L, null, false)
}
