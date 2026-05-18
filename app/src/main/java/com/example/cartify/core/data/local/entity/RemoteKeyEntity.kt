package com.example.cartify.core.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "remote_keys")
data class RemoteKeyEntity(
    @PrimaryKey val productId: Int,
    val prevSkip: Int?,
    val nextSkip: Int?
)
