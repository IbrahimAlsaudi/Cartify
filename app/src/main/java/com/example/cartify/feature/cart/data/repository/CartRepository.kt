package com.example.cartify.feature.cart.data.repository

import androidx.room.Query
import com.example.cartify.core.data.local.dao.CartDao
import com.example.cartify.core.data.local.entity.CartItemEntity
import com.example.cartify.core.data.toCartItemEntity
import com.example.cartify.core.data.toDomain
import com.example.cartify.core.domain.model.CartItem
import com.example.cartify.core.domain.model.Product
import com.example.cartify.core.domain.model.WishListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface CartRepository {
    fun getCartItems(): Flow<List<CartItem>>
    suspend fun addProductToCart(product: Product)
    suspend fun addWishlistItemToCart(wishlistItem: WishListItem)
    suspend fun removeFromCart(id: Int)
    suspend fun clearCart()
    suspend fun decreaseQuantity(itemId: Int)
    suspend fun increaseQuantity(itemId: Int)
    fun isCartItemExistsByProductId(productId: Int): Flow<Boolean>


}

class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao
): CartRepository {
    override fun getCartItems(): Flow<List<CartItem>> {
        return cartDao.getCartItems().map { list -> list.map { it.toDomain() }}
    }

    override suspend fun addProductToCart(product: Product) {
        cartDao.addToCart(product.toCartItemEntity())
    }

    override suspend fun addWishlistItemToCart(wishlistItem: WishListItem) {
        cartDao.addToCart(wishlistItem.toCartItemEntity())
    }

    override suspend fun removeFromCart(id: Int) {
        cartDao.removeFromCart(id)
    }

    override suspend fun clearCart() {
        cartDao.clearCart()
    }

    override suspend fun decreaseQuantity(itemId: Int) {
        cartDao.decreaseQuantity(itemId)
    }

    override suspend fun increaseQuantity(itemId: Int) {
        cartDao.increaseQuantity(itemId)
    }


    override fun isCartItemExistsByProductId(productId: Int): Flow<Boolean> {
        TODO("Not yet implemented")
    }


}