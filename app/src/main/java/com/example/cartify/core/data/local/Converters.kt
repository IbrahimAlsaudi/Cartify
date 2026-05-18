package com.example.cartify.core.data.local

import androidx.room.TypeConverter
import com.example.cartify.core.domain.model.OrderStatus
import kotlinx.serialization.json.Json

class Converters {
    @TypeConverter
    fun fromImageList(images: List<String>): String =
        Json.encodeToString(images)

    @TypeConverter
    fun toImageList(images: String): List<String> =
        Json.decodeFromString(images)

    @TypeConverter
    fun fromOrderStatus(status: OrderStatus): String =
        status.name

    @TypeConverter
    fun toOrderStatus(status: String): OrderStatus =
        OrderStatus.valueOf(status)
}