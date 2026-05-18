package com.example.cartify.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.cartify.core.data.local.entity.OrderItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderItemDao {
    @Query("""
        SELECT * FROM order_items
        WHERE orderId = :orderId
    """)
    fun getOrderItems(orderId: String): Flow<List<OrderItemEntity>>

    @Insert
    suspend fun insertOrderItems(orderItems: List<OrderItemEntity>)
}