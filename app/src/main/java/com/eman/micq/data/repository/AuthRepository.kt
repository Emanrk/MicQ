package com.eman.micq.data.repository

import com.eman.micq.data.model.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface AuthRepository {
    val currentUser: User?
    suspend fun signInAnonymously(): Result<User>
    fun signOut()
}

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override val currentUser: User?
        get() = firebaseAuth.currentUser?.let {
            User(uid = it.uid, displayName = it.displayName ?: "Anonymous", email = it.email ?: "")
        }

    override suspend fun signInAnonymously(): Result<User> {
        return try {
            val result = firebaseAuth.signInAnonymously().await()
            val firebaseUser = result.user ?: throw Exception("Sign in failed")
            Result.success(User(uid = firebaseUser.uid, displayName = "Anonymous"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}
