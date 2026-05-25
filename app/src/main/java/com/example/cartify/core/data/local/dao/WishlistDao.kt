package com.example.cartify.core.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.cartify.core.data.local.entity.WishlistItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WishlistDao {
    @Query("""
        SELECT * FROM wishlist_items
        ORDER BY addedAt DESC
    """)
    fun getWishListItems(): PagingSource<Int, WishlistItemEntity> /*The user maybe optimistic and wish to buy a lot of stuff later*/

    @Query("SELECT productId FROM wishlist_items")
    fun getAllWishlistIds(): Flow<List<Int>>
    @Insert
    suspend fun addToWishList(wishListItem: WishlistItemEntity)

    @Query("DELETE FROM wishlist_items WHERE productId = :id")
    suspend fun removeFromWishList(id: Int)

    @Query("""
            SELECT EXISTS( SELECT 1 FROM wishlist_items
            WHERE productId = :productId)
        """
    )
     fun isWishListed(productId: Int): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM wishlist_items WHERE productId = :productId)")
    suspend fun isWishlistedOnce(productId: Int): Boolean

    @Query("SELECT COUNT(productId) FROM wishlist_items")
    fun getWishlistedCount(): Flow<Int>

    @Query("DELETE FROM wishlist_items")
    suspend fun deleteAllWishlist()

    @Query("SELECT * FROM wishlist_items")
    suspend fun getAllWishlistItems(): List<WishlistItemEntity>
}