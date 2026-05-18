package com.example.cartify.core.domain.model

data class CartItem(
    val id: Int,
    val productId: Int,
    val title: String,
    val price: Double,
    val thumbnail: String,
    val quantity: Int,
    val addedAt: Long
)
