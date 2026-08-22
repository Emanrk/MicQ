package com.eman.micq.data.model

data class User(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val role: String = "", // Legacy role retained for backward compatibility
    val venueId: String? = null
)
