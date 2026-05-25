package com.example.cartify.feature.auth.data.repository

import android.util.Log
import com.example.cartify.core.data.firebase.FirebaseAuthSource
import com.example.cartify.core.data.firebase.FirestoreSource
import com.example.cartify.core.domain.model.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface AuthRepository {
    suspend fun signIn(email: String, password: String): Result<User>
    suspend fun signUp(email: String, password: String, name: String): Result<User>
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun signInAnonymously(): Result<User>
    suspend fun upgradeAnonymousAccount(email: String, password: String, name: String): Result<User>
    suspend fun upgradeAnonymousAccountWithGoogle(idToken: String): Result<User>
    fun isAnonymous(): Boolean
    suspend fun forgotPassword(email: String): Result<Unit>
    suspend fun signOut()
    suspend fun deleteAccount(): Result<Unit>
    fun getCurrentUser(): User?
    fun getAuthState(): Flow<User?>
    suspend fun reloadUser(): Result<Unit>
}

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuthSource: FirebaseAuthSource,
    private val firestoreSource: FirestoreSource
) : AuthRepository {

    override suspend fun signUp(
        email: String,
        password: String,
        name: String
    ): Result<User> {
        val result = firebaseAuthSource.signUp(email, password, name)
        result.onSuccess { user ->
            // Create Firestore document after successful registration
            firestoreSource.createUserDocument(user)
        }
        return result
    }

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        val result = firebaseAuthSource.signInWithGoogle(idToken)
        result.onSuccess { user ->
            // Check if document exists — if not create it
            val existingUser = firestoreSource.getUserDocument(user.id)
            if (existingUser == null) {
                firestoreSource.createUserDocument(user)
            }
        }
        return result
    }

    override suspend fun upgradeAnonymousAccount(
        email: String,
        password: String,
        name: String
    ): Result<User> {
        val result = firebaseAuthSource.upgradeAnonymousAccount(email, password, name)
        result.onSuccess { user ->
            // Create document — first time this user has a real account
            firestoreSource.createUserDocument(user)
        }
        return result
    }

    override suspend fun upgradeAnonymousAccountWithGoogle(idToken: String): Result<User> {
        val result = firebaseAuthSource.upgradeAnonymousAccountWithGoogle(idToken)
        result.onSuccess { user ->
            val existingUser = firestoreSource.getUserDocument(user.id)
            if (existingUser == null) {
                firestoreSource.createUserDocument(user)
            }
        }
        return result
    }

    override suspend fun deleteAccount(): Result<Unit> {
        return try {
            val uid = firebaseAuthSource.getCurrentUser()?.id
                ?: return Result.failure(Exception("No user logged in"))

            // 1. delete Firestore first
            val firestoreResult = firestoreSource.deleteUser(uid)
            if (firestoreResult.isFailure) {
                return Result.failure(firestoreResult.exceptionOrNull()
                    ?: Exception("Failed to delete user data"))
            }

            // 2. only delete auth if Firestore succeeded
            firebaseAuthSource.deleteAccount()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Pass through — no Firestore interaction needed
    override suspend fun signIn(email: String, password: String): Result<User> =
        firebaseAuthSource.signIn(email, password)

    override suspend fun signInAnonymously(): Result<User> =
        firebaseAuthSource.signInAnonymously()

    override suspend fun forgotPassword(email: String): Result<Unit> =
        firebaseAuthSource.forgotPassword(email)

    override suspend fun signOut() =
        firebaseAuthSource.signOut()

    override fun getCurrentUser(): User? =
        firebaseAuthSource.getCurrentUser()

    override fun getAuthState(): Flow<User?> =
        firebaseAuthSource.getAuthState()

    override fun isAnonymous(): Boolean =
        firebaseAuthSource.isAnonymous()

    override suspend fun reloadUser(): Result<Unit> =
        firebaseAuthSource.reloadUser()
}