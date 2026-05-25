package com.example.cartify.core.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.cartify.core.data.local.entity.CartItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("""
        SELECT * FROM cart_items
        ORDER BY addedAt DESC
    """)
    fun getCartItems(): Flow<List<CartItemEntity>> /*No need for pagination the user is not elon musk and global economy is broken*/

    // in DAO
    @Query("SELECT * FROM cart_items WHERE productId = :productId")
    suspend fun getCartItemById(productId: Int): CartItemEntity?
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addToCart(product: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE productId = :productId")
    suspend fun removeFromCart(productId: Int)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    @Query("""
        UPDATE cart_items
        SET quantity = quantity + 1
        WHERE productId = :productId
    """)
    suspend fun increaseQuantity(productId: Int)

    @Query(
        """
        UPDATE cart_items
        SET quantity = quantity - 1
        WHERE productId = :productId
    """
    )
    suspend fun decreaseQuantity(productId: Int)

    @Query("""
        SELECT EXISTS (SELECT 1 FROM cart_items
        WHERE productId = :productId)
    """)
    fun isCartItemExistsByProductId(productId: Int): Flow<Boolean>

    // in CartDao
    @Query("SELECT * FROM cart_items")
    suspend fun getAllCartItemsToSyncWithFirestore(): List<CartItemEntity>
}
