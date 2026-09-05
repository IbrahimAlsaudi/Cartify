package com.example.cartify.feature.orders.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.cartify.core.data.firebase.FirestoreSource
import com.example.cartify.core.data.local.dao.CartDao
import com.example.cartify.core.data.local.dao.OrderDao
import com.example.cartify.core.data.local.dao.OrderItemDao
import com.example.cartify.core.data.toDomain
import com.example.cartify.core.data.toOrderEntity
import com.example.cartify.core.data.toEntity
import com.example.cartify.core.domain.model.Order
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface OrderRepository {
    fun getOrders(): Flow<PagingData<Order>>
    suspend fun addOrder(order: Order): Result<Unit>
    fun getOrderById(orderId: String): Flow<Order?>
}

class OrderRepositoryImpl @Inject constructor(
    private val orderDao: OrderDao,
    private val orderItemDao: OrderItemDao,
    private val cartDao: CartDao,
    private val firestoreSource: FirestoreSource,
    private val auth: FirebaseAuth
): OrderRepository {

    private val userId get() = auth.currentUser?.uid
    override fun getOrders(): Flow<PagingData<Order>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20, enablePlaceholders = false
            ),
            pagingSourceFactory = orderDao::getOrders
        ).flow.map { pagingData ->
            pagingData.map { entity -> entity.toDomain() }
        }
    }

    override suspend fun addOrder(order: Order): Result<Unit> {
        return try {
            val orderEntity = order.toOrderEntity()
            val itemEntities = order.items.map { it.toEntity(order.id) }
            orderDao.insertOrder(orderEntity)
            orderItemDao.insertOrderItems(itemEntities)
            cartDao.clearCart()
            userId?.let {
                firestoreSource.createOrder(it,orderEntity, itemEntities)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun getOrderById(orderId: String): Flow<Order?> {
        return combine(
            orderDao.getOrderByIdFlow(orderId),
            orderItemDao.getOrderItems(orderId)
        ) { orderEntity, itemEntities ->
            orderEntity?.toDomain()?.copy(
                items = itemEntities.map { it.toDomain() }
            )
        }
    }

}
