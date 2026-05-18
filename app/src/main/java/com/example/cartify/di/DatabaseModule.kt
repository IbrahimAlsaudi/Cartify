package com.example.cartify.di

import android.content.Context
import androidx.room.Room
import com.example.cartify.core.data.local.CartifyDatabase
import com.example.cartify.core.data.local.dao.CartDao
import com.example.cartify.core.data.local.dao.OrderDao
import com.example.cartify.core.data.local.dao.OrderItemDao
import com.example.cartify.core.data.local.dao.ProductDao
import com.example.cartify.core.data.local.dao.RemoteKeyDao
import com.example.cartify.core.data.local.dao.WishlistDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): CartifyDatabase {
        return Room.databaseBuilder(
            context = context,
            klass = CartifyDatabase::class.java,
            name = "cartify_database"
        )
            .fallbackToDestructiveMigration(true)
            .build()
    }


    @Provides
    fun provideProductDao(db: CartifyDatabase): ProductDao = db.productDao()

    @Provides
    fun provideCartDao(db: CartifyDatabase): CartDao = db.cartDao()

    @Provides
    fun provideWishlistDao(db: CartifyDatabase): WishlistDao = db.wishlistDao()

    @Provides
    fun provideOrderDao(db: CartifyDatabase): OrderDao = db.orderDao()

    @Provides
    fun provideOrderItemDao(db: CartifyDatabase): OrderItemDao = db.orderItemDao()

    @Provides
    fun provideRemoteKeyDao(db: CartifyDatabase): RemoteKeyDao = db.remoteKeyDao()
}