package com.example.cartify.core.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.cartify.core.data.local.dao.CartDao
import com.example.cartify.core.data.local.dao.OrderDao
import com.example.cartify.core.data.local.dao.OrderItemDao
import com.example.cartify.core.data.local.dao.ProductDao
import com.example.cartify.core.data.local.dao.RemoteKeyDao
import com.example.cartify.core.data.local.dao.WishlistDao
import com.example.cartify.core.data.local.entity.CartItemEntity
import com.example.cartify.core.data.local.entity.OrderEntity
import com.example.cartify.core.data.local.entity.OrderItemEntity
import com.example.cartify.core.data.local.entity.ProductEntity
import com.example.cartify.core.data.local.entity.RemoteKeyEntity
import com.example.cartify.core.data.local.entity.WishlistItemEntity

@Database(
    entities = [
        CartItemEntity::class,
        OrderEntity::class,
        OrderItemEntity::class,
        ProductEntity::class,
        RemoteKeyEntity::class,
        WishlistItemEntity::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class CartifyDatabase: RoomDatabase() {
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao
    abstract fun orderItemDao(): OrderItemDao
    abstract fun productDao(): ProductDao
    abstract fun remoteKeyDao(): RemoteKeyDao
    abstract fun wishlistDao(): WishlistDao
}