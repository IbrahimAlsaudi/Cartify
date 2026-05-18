package com.example.cartify.core.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProductResponseDto(
    val products: List<ProductDto>,
    val total: Int,
    val skip: Int,
    val limit: Int
)
