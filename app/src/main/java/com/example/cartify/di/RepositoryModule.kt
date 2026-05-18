package com.example.cartify.di

import com.example.cartify.feature.cart.data.repository.CartRepository
import com.example.cartify.feature.cart.data.repository.CartRepositoryImpl
import com.example.cartify.feature.home.data.repository.ProductRepository
import com.example.cartify.feature.home.data.repository.ProductRepositoryImpl
import com.example.cartify.feature.wishlist.data.repository.WishlistRepository
import com.example.cartify.feature.wishlist.data.repository.WishlistRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds @Singleton
    abstract fun bindProductRepository(
        impl: ProductRepositoryImpl
    ): ProductRepository

    @Binds
    @Singleton
    abstract fun bindWishlistRepository(
        impl: WishlistRepositoryImpl
    ): WishlistRepository

    @Binds
    @Singleton
    abstract fun bindCartItemRepository(
        impl: CartRepositoryImpl
    ): CartRepository
}