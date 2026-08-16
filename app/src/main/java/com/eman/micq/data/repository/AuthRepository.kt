package com.eman.micq.data.repository

import com.eman.micq.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

interface AuthRepository {
    val currentUser: User?
    suspend fun fetchCurrentUserWithRole(): User?
    suspend fun signInAnonymously(): Result<User>
    suspend fun signUp(name: String, email: String, password: String, role: String): Result<User>
    suspend fun signIn(email: String, password: String): Result<User>
    fun signOut()
}

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val database: FirebaseDatabase
) : AuthRepository {

    override val currentUser: User?
        get() = firebaseAuth.currentUser?.let {
            User(uid = it.uid, displayName = it.displayName ?: "Anonymous", email = it.email ?: "")
        }

    override suspend fun fetchCurrentUserWithRole(): User? {
        val firebaseUser = firebaseAuth.currentUser ?: return null
        return try {
            val snapshot = database.getReference("users").child(firebaseUser.uid).get().await()
            snapshot.getValue(User::class.java)
        } catch (e: Exception) {
            // Fallback to basic user if DB fetch fails
            User(uid = firebaseUser.uid, displayName = firebaseUser.displayName ?: "Anonymous", email = firebaseUser.email ?: "")
        }
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

    override suspend fun signUp(name: String, email: String, password: String, role: String): Result<User> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Registration failed")
            
            val user = User(
                uid = firebaseUser.uid,
                displayName = name,
                email = email,
                role = role
            )
            
            database.getReference("users").child(firebaseUser.uid).setValue(user).await()
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Login failed")
            
            val snapshot = database.getReference("users").child(firebaseUser.uid).get().await()
            val user = snapshot.getValue(User::class.java) ?: throw Exception("User data not found")
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun signOut() {
        firebaseAuth.signOut()
    }
}
