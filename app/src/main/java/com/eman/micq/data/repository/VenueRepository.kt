package com.eman.micq.data.repository

import com.eman.micq.data.model.Venue
import com.eman.micq.data.model.VenueMember
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface VenueRepository {
    suspend fun createVenue(
        venue: Venue,
        ownerId: String
    ): Result<Venue>

    suspend fun addMember(
        venueId: String,
        userId: String,
        role: String
    ): Result<Unit>

    suspend fun getVenueMembers(
        venueId: String
    ): Result<List<VenueMember>>
}

@Singleton
class VenueRepositoryImpl @Inject constructor(
    private val database: FirebaseDatabase
) : VenueRepository {

    override suspend fun createVenue(venue: Venue, ownerId: String): Result<Venue> {
        return try {
            val rootRef = database.reference
            
            // Safely generate a unique venueId using Firebase push().key
            val venueId = rootRef.child("venues").push().key 
                ?: throw Exception("Could not generate venue ID")
            
            val finalVenue = venue.copy(id = venueId)
            
            val initialMember = VenueMember(
                id = ownerId,
                venueId = venueId,
                userId = ownerId,
                role = VenueMember.ROLE_OWNER
            )

            // ONE atomic rootRef.updateChildren() operation
            val updates = hashMapOf<String, Any?>(
                "venues/$venueId" to finalVenue,
                "venueMembers/$venueId/$ownerId" to initialMember,
                "userMemberships/$ownerId/$venueId" to true
            )

            rootRef.updateChildren(updates).await()
            Result.success(finalVenue)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addMember(venueId: String, userId: String, role: String): Result<Unit> {
        return try {
            // Validate the role before writing
            val allowedRoles = listOf(
                VenueMember.ROLE_OWNER,
                VenueMember.ROLE_MANAGER,
                VenueMember.ROLE_DJ,
                VenueMember.ROLE_PERFORMER
            )
            
            if (role !in allowedRoles) {
                return Result.failure(Exception("Invalid role: $role"))
            }

            val rootRef = database.reference
            
            val member = VenueMember(
                id = userId,
                venueId = venueId,
                userId = userId,
                role = role
            )

            // Atomically update venueMembers and userMemberships
            val updates = hashMapOf<String, Any?>(
                "venueMembers/$venueId/$userId" to member,
                "userMemberships/$userId/$venueId" to true
            )

            rootRef.updateChildren(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getVenueMembers(venueId: String): Result<List<VenueMember>> {
        return try {
            val snapshot = database.getReference("venueMembers/$venueId").get().await()
            val members = snapshot.children.mapNotNull { it.getValue(VenueMember::class.java) }
            Result.success(members)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
