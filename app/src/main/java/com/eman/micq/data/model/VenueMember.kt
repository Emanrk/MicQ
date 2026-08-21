package com.eman.micq.data.model

/**
 * Defines the relationship between a User and a Venue.
 * Supports multi-tenancy by allowing a user to have different roles at different venues.
 */
data class VenueMember(
    val id: String = "",        // Unique identifier for the membership record
    val venueId: String = "",   // Reference to Venue.id
    val userId: String = "",    // Reference to User.uid
    val role: String = "",      // OWNER, MANAGER, DJ, or PERFORMER
    val joinedAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
) {
    companion object {
        const val ROLE_OWNER = "OWNER"
        const val ROLE_MANAGER = "MANAGER"
        const val ROLE_DJ = "DJ"
        const val ROLE_PERFORMER = "PERFORMER"
    }
}
