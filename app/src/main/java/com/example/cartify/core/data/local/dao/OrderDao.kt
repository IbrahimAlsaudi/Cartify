package com.example.cartify.core.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Upsert
import com.example.cartify.core.data.local.entity.OrderEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface OrderDao {
    @Query("""
        SELECT * FROM orders
        ORDER BY createdAt DESC
    """)
    fun getOrders(): PagingSource<Int, OrderEntity> /* Room-generated PagingSource the key type is always Int — it represents the row offset, not a page number. Room uses it internally to track position*/

    @Query("UPDATE orders SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: String, status: String)

    @Insert
    suspend fun insertOrder(order: OrderEntity)

    @Upsert
    suspend fun upsertOrders(orders: List<OrderEntity>) /*To sync from firebase to room*/

    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderById(orderId: String): OrderEntity

    @Query("SELECT * FROM orders WHERE id = :orderId")
    fun getOrderByIdFlow(orderId: String): Flow<OrderEntity?>

    @Query("""
        SELECT * FROM orders
        WHERE id 
        LIKE '%' || :orderId || '%'
        ORDER BY createdAt DESC
    """)
     fun searchOrders(orderId: String): Flow<List<OrderEntity>>
}