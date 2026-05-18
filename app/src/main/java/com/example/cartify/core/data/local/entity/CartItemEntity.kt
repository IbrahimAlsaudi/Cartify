package com.example.cartify.core.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items",
    indices = [Index(value = ["productId"], unique = true)])
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val productId: Int ,
    val title: String,
    val price: Double,
    val thumbnail: String,
    val quantity: Int,
    val addedAt: Long
)
