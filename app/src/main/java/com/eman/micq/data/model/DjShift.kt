package com.eman.micq.data.model

data class DjShift(
    val id: String = "",
    val djId: String = "",
    val djName: String = "",
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null,
    val lastActiveAt: Long = System.currentTimeMillis(),
    val events: Map<String, ShiftEvent> = emptyMap()
)

data class ShiftEvent(
    val type: String = "", // DISCONNECT, RECONNECT
    val timestamp: Long = System.currentTimeMillis()
)
