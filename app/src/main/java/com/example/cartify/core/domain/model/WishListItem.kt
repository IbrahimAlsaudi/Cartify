package com.example.cartify.core.domain.model

data class WishListItem(
    val productId: Int,
    val title: String,
    val price: Double,
    val thumbnail: String,
    val addedAt: Long
)
