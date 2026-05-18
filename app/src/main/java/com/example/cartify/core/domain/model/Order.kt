package com.example.cartify.core.domain.model

data class Order(
    val id: String,
    val userId: String,
    val totalPrice: Double,
    val status: OrderStatus,
    val createdAt: Long,
    val items: List<OrderItem>
)

enum class OrderStatus {
    PENDING,
    CONFIRMED,
    SHIPPED,
    DELIVERED,
    CANCELLED
}

data class OrderItem(
    val productId: Int,
    val title: String,
    val price: Double,
    val thumbnail: String,
    val quantity: Int
)