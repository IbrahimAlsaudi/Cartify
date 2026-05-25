package com.example.cartify.core.data.firebase

import androidx.credentials.CredentialManager
import com.example.cartify.core.domain.model.User
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    suspend fun signIn(email: String, password: String): Result<User> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Result.failure(Exception("User not found"))
            Result.success(firebaseUser.toDomain())
        } catch (e: Exception) {
            if(e is CancellationException) throw e
            Result.failure(e)
        }
    }

    suspend fun signUp(email: String, password: String, name: String): Result<User> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = result.user ?: return Result.failure(Exception("User not found"))
            val profileUpdate = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            firebaseUser.updateProfile(profileUpdate).await()
            Result.success(firebaseUser.toDomain())
        } catch (e: Exception) {
            if(e is CancellationException) throw e
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = result.user ?: return Result.failure(Exception("User not found"))
            Result.success(firebaseUser.toDomain())
        } catch (e: Exception) {
            if(e is CancellationException) throw e
            Result.failure(e)
        }
    }

    suspend fun signInAnonymously(): Result<User> {
        return try {
            val result = firebaseAuth.signInAnonymously().await()
            val firebaseUser = result.user
                ?: return Result.failure(Exception("Anonymous sign in failed"))
            Result.success(firebaseUser.toDomain())
        } catch (e: Exception) {
            if(e is CancellationException) throw e
            Result.failure(e)
        }
    }

    suspend fun upgradeAnonymousAccount(
        email: String,
        password: String,
        name: String
    ): Result<User> {
        return try {
            val credential = EmailAuthProvider.getCredential(email, password)
            val currentUser = firebaseAuth.currentUser
                ?: return Result.failure(Exception("No current user"))

            // Link anonymous account with email credential
            val result = currentUser.linkWithCredential(credential).await()
            val firebaseUser = result.user
                ?: return Result.failure(Exception("Link failed"))

            // Set display name
            val profileUpdate = UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build()
            firebaseUser.updateProfile(profileUpdate).await()

            Result.success(firebaseUser.toDomain())
        } catch (e: Exception) {
            if(e is CancellationException) throw e
            Result.failure(e)
        }
    }

    suspend fun upgradeAnonymousAccountWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val currentUser = firebaseAuth.currentUser
                ?: return Result.failure(Exception("No current user"))

            val result = try {
                currentUser.linkWithCredential(credential).await()
            } catch (e: Exception) {
                if (e is com.google.firebase.auth.FirebaseAuthUserCollisionException) {
                    // This Google account is already linked to another user.
                    // Instead of failing, we sign in to that existing account.
                    firebaseAuth.signInWithCredential(credential).await()
                } else {
                    throw e
                }
            }

            val firebaseUser = result.user
                ?: return Result.failure(Exception("Link/Sign-in failed"))

            Result.success(firebaseUser.toDomain())
        } catch (e: Exception) {
            if(e is CancellationException) throw e
            Result.failure(e)
        }
    }

    fun isAnonymous(): Boolean = firebaseAuth.currentUser?.isAnonymous ?: false

    suspend fun forgotPassword(email: String): Result<Unit> {
        return try {
            firebaseAuth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            if(e is CancellationException) throw e
            Result.failure(e)
        }
    }

    fun signOut() {
        firebaseAuth.signOut()
    }

    suspend fun deleteAccount(): Result<Unit> {
        return try {
            firebaseAuth.currentUser?.delete()?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            if (e is CancellationException) throw e
            Result.failure(e)
        }
    }

    fun getCurrentUser(): User? = firebaseAuth.currentUser?.toDomain()

    fun getAuthState(): Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.toDomain())
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }

    suspend fun reloadUser(): Result<Unit> {
        return try {
            firebaseAuth.currentUser?.reload()?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun FirebaseUser.toDomain(): User = User(
        id = uid,
        name = displayName ?: "Guest",
        email = email ?: "",
        profilePicture = photoUrl?.toString(),
        isAnonymous = isAnonymous
    )
}