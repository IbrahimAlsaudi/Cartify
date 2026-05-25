package com.example.cartify.core.domain.model

data class User(
    val id: String,
    val name: String,
    val email: String,
    val profilePicture: String?,
    val isAnonymous: Boolean = false  // ← add this
)