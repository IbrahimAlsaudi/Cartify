package com.example.cartify.core.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.cartify.core.data.local.entity.CartItemEntity
import com.example.cartify.core.data.local.entity.ProductEntity
import com.example.cartify.core.domain.model.CartItem
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Query("""
        SELECT * FROM cart_items
        ORDER BY addedAt DESC
    """)
    fun getCartItems(): Flow<List<CartItemEntity>> /*No need for pagination the user is not elon musk and global economy is broken*/

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addToCart(product: CartItemEntity)

    @Query("DELETE FROM cart_items WHERE id = :id")
    suspend fun removeFromCart(id: Int)

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()

    @Query("""
        UPDATE cart_items
        SET quantity = quantity + 1
        WHERE id = :itemId
    """)
    suspend fun increaseQuantity(itemId: Int)

    @Query("""
        UPDATE cart_items
        SET quantity = quantity - 1
        WHERE id = :itemId
    """)
    suspend fun decreaseQuantity(itemId: Int)

    @Query("""
        SELECT EXISTS (SELECT 1 FROM cart_items
        WHERE productId = :productId)
    """)
    fun isCartItemExistsByProductId(productId: Int): Flow<Boolean>

}
