package com.example.cartify.feature.auth.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cartify.feature.auth.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(SplashStateUi())
    val uiState = _uiState.asStateFlow()

    init {
        checkAuthState()
    }


    private fun checkAuthState() {
        viewModelScope.launch {
            val reloadResult = authRepository.reloadUser()
            reloadResult.fold(
                onSuccess = {
                    val currentUser = authRepository.getCurrentUser()
                    if (currentUser != null) {
                        _uiState.update { it.copy(navigateTo = NavigateTo.MAIN) }
                    } else {
                        _uiState.update { it.copy(navigateTo = NavigateTo.LOGIN) }
                    }
                },
                onFailure = { exception ->
                    // Check if failure is due to no internet connection
                    val isNetworkError = exception.message?.contains("network", ignoreCase = true) == true || 
                                       exception is java.net.UnknownHostException

                    if (isNetworkError) {
                        // If offline, check if we have a locally cached user
                        val currentUser = authRepository.getCurrentUser()
                        if (currentUser != null) {
                            _uiState.update { it.copy(navigateTo = NavigateTo.MAIN) }
                        } else {
                            _uiState.update { it.copy(navigateTo = NavigateTo.LOGIN) }
                        }
                    } else {
                        // User was deleted, token invalid, or other critical error
                        authRepository.signOut()
                        _uiState.update { it.copy(navigateTo = NavigateTo.LOGIN) }
                    }
                }
            )
        }
    }
}

data class SplashStateUi(
    val navigateTo: NavigateTo? = null
)

enum class NavigateTo {
    MAIN,
    LOGIN
}