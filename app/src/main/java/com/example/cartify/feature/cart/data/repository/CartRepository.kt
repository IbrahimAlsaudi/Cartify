package com.example.cartify.feature.cart.data.repository

import com.example.cartify.core.data.firebase.FirestoreSource
import com.example.cartify.core.data.local.dao.CartDao
import com.example.cartify.core.data.local.entity.CartItemEntity
import com.example.cartify.core.data.toCartItemEntity
import com.example.cartify.core.data.toDomain
import com.example.cartify.core.domain.model.CartItem
import com.example.cartify.core.domain.model.Product
import com.example.cartify.core.domain.model.WishListItem
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface CartRepository {

    fun getCartItems(): Flow<List<CartItem>>

    suspend fun addProductToCart(product: Product): Result<Unit>

    suspend fun addWishlistItemToCart(wishlistItem: WishListItem): Result<Unit>

    suspend fun removeFromCart(productId: Int): Result<Unit>

    suspend fun clearCart(): Result<Unit>

    suspend fun decreaseQuantity(productId: Int)

    suspend fun increaseQuantity(productId: Int)

    suspend fun syncCartFromFirestore(): Result<Unit>

    suspend fun mergeLocalDataWithCloud(): Result<Unit>

    fun isCartItemExistsByProductId(productId: Int): Flow<Boolean>
}
class CartRepositoryImpl @Inject constructor(
    private val cartDao: CartDao,
    private val firestoreSource: FirestoreSource,
    private val auth: FirebaseAuth
) : CartRepository {

    private val userId get() = auth.currentUser?.uid

    override fun getCartItems(): Flow<List<CartItem>> {
        return cartDao.getCartItems().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun addProductToCart(product: Product): Result<Unit> {
        return try {
            val entity = product.toCartItemEntity()
            cartDao.addToCart(entity)

            // Since we know the user MUST be logged in to be here, 
            // we just use the UID. If it's null (which shouldn't happen), 
            // the Firestore call simply won't run.
            userId?.let { uid ->
                firestoreSource.addToCart(uid, entity)
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun addWishlistItemToCart(wishlistItem: WishListItem): Result<Unit> {
        return try {
            val entity = wishlistItem.toCartItemEntity()
            cartDao.addToCart(entity)
            userId?.let { uid ->
                firestoreSource.addToCart(uid, entity)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun removeFromCart(productId: Int): Result<Unit> {
        return try {
            cartDao.removeFromCart(productId)
            userId?.let { uid ->
                firestoreSource.removeFromCart(uid, productId)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }


    override suspend fun decreaseQuantity(productId: Int) {
        val currentItem = cartDao.getCartItemById(productId) ?: return
        if (currentItem.quantity > 1) {
            cartDao.decreaseQuantity(productId)
            val updatedItem = cartDao.getCartItemById(productId) ?: return
            userId?.let { uid ->
                firestoreSource.updateCartQuantity(uid, productId, updatedItem.quantity)
            }
        }
    }

    override suspend fun increaseQuantity(productId: Int) {
        cartDao.increaseQuantity(productId)
        val updatedItem = cartDao.getCartItemById(productId) ?: return
        userId?.let { uid ->
            firestoreSource.updateCartQuantity(uid, productId, updatedItem.quantity)
        }
    }

    override suspend fun clearCart():Result<Unit>{
        return try {
            cartDao.clearCart()
            userId?.let { uid ->
                firestoreSource.clearCart(uid)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun syncCartFromFirestore(): Result<Unit> {
        return try {
            val uid = userId ?: return Result.success(Unit)
            val cloudItems = firestoreSource.getCart(uid)
            cartDao.clearCart()
            cloudItems.forEach { cartDao.addToCart(it) }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun mergeLocalDataWithCloud(): Result<Unit> {
        return try {
            val uid = userId ?: return Result.success(Unit)
            val localItems = cartDao.getAllCartItemsToSyncWithFirestore()
            localItems.forEach { item ->
                firestoreSource.addToCart(uid, item)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun isCartItemExistsByProductId(productId: Int): Flow<Boolean> {
        return cartDao.isCartItemExistsByProductId(productId)
    }


}