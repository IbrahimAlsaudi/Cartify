package com.example.cartify.feature.auth.data.repository

import com.example.cartify.core.data.firebase.FirebaseAuthSource
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
    private val firebaseAuthSource: FirebaseAuthSource
) : AuthRepository {

    override suspend fun signIn(email: String, password: String): Result<User> =
        firebaseAuthSource.signIn(email, password)

    override suspend fun signUp(email: String, password: String, name: String): Result<User> =
        firebaseAuthSource.signUp(email, password, name)

    override suspend fun signInWithGoogle(idToken: String): Result<User> =
        firebaseAuthSource.signInWithGoogle(idToken)

    override suspend fun signInAnonymously(): Result<User> = firebaseAuthSource.signInAnonymously()

    override suspend fun upgradeAnonymousAccount(
        email: String,
        password: String,
        name: String
    ): Result<User> = firebaseAuthSource.upgradeAnonymousAccount(email,password,name)

    override suspend fun upgradeAnonymousAccountWithGoogle(idToken: String): Result<User> = firebaseAuthSource.upgradeAnonymousAccountWithGoogle(idToken)

    override fun isAnonymous(): Boolean = firebaseAuthSource.isAnonymous()

    override suspend fun forgotPassword(email: String): Result<Unit> =
        firebaseAuthSource.forgotPassword(email)

    override suspend fun signOut() =
        firebaseAuthSource.signOut()

    override suspend fun deleteAccount(): Result<Unit> =
        firebaseAuthSource.deleteAccount()

    override fun getCurrentUser(): User? =
        firebaseAuthSource.getCurrentUser()

    override fun getAuthState(): Flow<User?> =
        firebaseAuthSource.getAuthState()

    override suspend fun reloadUser(): Result<Unit> = firebaseAuthSource.reloadUser()
}