package com.example.cartify.feature.auth.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cartify.core.data.firebase.FirestoreSource
import com.example.cartify.feature.auth.data.repository.AuthRepository
import com.example.cartify.core.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val firestoreSource: FirestoreSource
) : ViewModel() {
    val authState: StateFlow<User?> = authRepository.getAuthState()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = authRepository.getCurrentUser()
        )

    fun logout() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }

    fun deleteAccount() {
        val uid = authRepository.getCurrentUser()?.id
        viewModelScope.launch {
            uid?.let {
                firestoreSource.deleteUser(uid)
                Log.d("UserId: ", uid)
            }
            authRepository.deleteAccount()
        }
    }
}
