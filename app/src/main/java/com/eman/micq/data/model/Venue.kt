package com.eman.micq.data.model

/**
 * Represents a business entity (e.g., Pub, Restaurant) within the MicQ platform.
 * Supports multi-tenancy by acting as the root container for sessions and members.
 */
data class Venue(
    val id: String = "",
    val name: String = "",
    val address: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)
