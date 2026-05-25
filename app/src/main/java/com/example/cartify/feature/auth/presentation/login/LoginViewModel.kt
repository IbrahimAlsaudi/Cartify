package com.example.cartify.feature.auth.presentation.login

import android.app.Activity
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cartify.core.util.GoogleSignInHelper
import com.example.cartify.feature.auth.data.repository.AuthRepository
import com.example.cartify.feature.cart.data.repository.CartRepository
import com.example.cartify.feature.wishlist.data.repository.WishlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val cartRepository: CartRepository,
    private val wishlistRepository: WishlistRepository,
    private val googleSignInHelper: GoogleSignInHelper
): ViewModel() {


    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onEmailChanged(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChanged(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onIsPasswordChanged(isPasswordShown: Boolean) {
        _uiState.update { it.copy(isPasswordShown = !isPasswordShown) }
    }

    fun loginWithEmailAndPassword(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val result = authRepository.signIn(email, password)
            result.fold(
                onSuccess = {
//                    val isAnonymous = authRepository.isAnonymous()
//                    val firebaseResult = if (isAnonymous) {
//                        authRepository.upgradeAnonymousAccount(
//                            email, password,
//                            name = TODO(),
//                        )
//                    } else {
//                        authRepository.signInWithGoogle(idToken)
//                    }

                    // Sync after successful login
                    cartRepository.syncCartFromFirestore()
                    wishlistRepository.syncWishlistFromFirestore()
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = when {
                                it.email.isBlank() && it.password.isBlank() -> "Get out of my app right now"
                                it.email.isBlank() -> "Email is Required"
                                it.password.isBlank() -> "Really, No password ?"
                                e.message?.contains("badly formatted.") == true -> "Do you know what does email means?"
//                                !it.email.contains("@") -> "Wrong Email format"
//                                e.message?.contains("empty") == true -> "All fields are required !"
                                e.message?.contains("password") == true -> "Wrong password"
                                e.message?.contains("no user") == true -> "No account found"
                                e.message?.contains("network") == true -> "No internet connection"
                                e.message?.contains("incorrect") == true -> "Incorrect Email or password"
                                else -> "Login failed. Please try again"
                            }
                        )
                    }
                    Log.d("LoginViewModel", e.message ?: "Unknown")
                }
            )
        }
    }

    fun joinAsGuest() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // If already anonymous, just succeed. Otherwise sign in anonymously.
            if (authRepository.isAnonymous()) {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                return@launch
            }

            val result = authRepository.signInAnonymously()
            result.fold(
                onSuccess = {
//                    cartRepository.clearCart()
//                    wishlistRepository.deleteAllWishlist()
                    cartRepository.syncCartFromFirestore()
                    wishlistRepository.syncWishlistFromFirestore()
                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                },
                onFailure = { e ->
                    _uiState.update {
                        it.copy(isLoading = false, error = "Failed to continue as guest")
                    }
                }
            )
        }
    }


    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    fun loginWithGoogle(activity: Activity) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }

                // Step 1 — get Google ID token
                val googleResult = googleSignInHelper.signIn(activity)
                googleResult.fold(
                    onSuccess = { idToken ->
                        // Step 2 — pass to Firebase
                        val isAnonymous = authRepository.isAnonymous()
                        val firebaseResult = if (isAnonymous) {
                            authRepository.upgradeAnonymousAccountWithGoogle(idToken)
                        } else {
                            authRepository.signInWithGoogle(idToken)
                        }

                        firebaseResult.fold(
                            onSuccess = {
                                viewModelScope.launch {
                                    if (isAnonymous) {
                                        // Merge local data to new/existing account
                                        cartRepository.mergeLocalDataWithCloud()
                                        wishlistRepository.mergeLocalDataWithCloud()
                                    }
                                    // Final sync to get combined data
                                    cartRepository.syncCartFromFirestore()
                                    wishlistRepository.syncWishlistFromFirestore()
                                    _uiState.update { it.copy(isLoading = false, isSuccess = true) }
                                }
                            },
                            onFailure = { e ->
                                _uiState.update {
                                    it.copy(isLoading = false, error = "Google sign in failed: ${e.message}")
                                }
                                Log.d("GoogleSignIn", e.localizedMessage, e)
                            }
                        )
                    },
                    onFailure = { e ->
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = when (e.message) {
                                    "No Google accounts found on this device" -> "No Google accounts found. Please add one in settings."
                                    "Sign in cancelled" -> "Sign in cancelled"
                                    "Sign in interrupted" -> "Sign in was interrupted. Please try again."
                                    "Google Sign-In is not properly configured" -> "Google Sign-In configuration error. Please contact support."
                                    "An unknown error occurred during sign in" -> "An unknown error occurred. Please try again."
                                    else -> "Google sign in failed: ${e.message}"
                                }
                            )
                        }
                        Log.e("LoginViewModel", "Google Sign-In Error", e)
                    }
                )
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "An unexpected error occurred") }
                Log.e("LoginViewModel", "Fatal error in loginWithGoogle", e)
            }
        }
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isPasswordShown: Boolean = false,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false
)