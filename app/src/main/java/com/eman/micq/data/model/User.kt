package com.eman.micq.data.model

data class User(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val role: String = "" // ADMIN, DJ, or PERFORMER
)
