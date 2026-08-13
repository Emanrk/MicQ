package com.eman.micq.data.model

data class Session(
    val id: String = "",
    val isActive: Boolean = false,
    val currentDjId: String? = null
)
