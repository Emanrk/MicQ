package com.eman.micq.data.model

data class DjShift(
    val id: String = "",
    val djId: String = "",
    val djName: String = "",
    val startTime: Long = System.currentTimeMillis(),
    val endTime: Long? = null
)
