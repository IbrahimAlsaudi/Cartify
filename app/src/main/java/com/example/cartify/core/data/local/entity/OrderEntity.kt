package com.example.cartify.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class OrderEntity(
    @PrimaryKey val id: String,
    val userId: String,
    val totalPrice: Double,
    val status: String,
    val createdAt: Long,
    val deliveryAddress: String,
    val paymentMethod: String
)