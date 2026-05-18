package com.example.cartify.feature.wishlist.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.cartify.core.data.local.dao.WishlistDao
import com.example.cartify.core.data.toDomain
import com.example.cartify.core.data.toWishlistEntity
import com.example.cartify.core.domain.model.Product
import com.example.cartify.core.domain.model.WishListItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface WishlistRepository {
    suspend fun toggleWishlist(product: Product)
    fun getWishlistedIds(): Flow<Set<Int>>
    fun isWishlisted(productId: Int): Flow<Boolean>
    fun getWishlistItems(): Flow<PagingData<WishListItem>>
    suspend fun syncWishlist(userId: String)
    fun getWishlistCount(): Flow<Int>
    suspend fun removeFromWishlist(productId: Int)

    suspend fun deleteAllWishlist()
}

class WishlistRepositoryImpl @Inject constructor(
    private val wishlistDao: WishlistDao
): WishlistRepository {
    override suspend fun toggleWishlist(product: Product) {
        if(wishlistDao.isWishlistedOnce(product.id)) {
            wishlistDao.removeFromWishList(product.id)
        } else {
            wishlistDao.addToWishList(product.toWishlistEntity())
        }
    }

    override fun getWishlistedIds(): Flow<Set<Int>> {
        return wishlistDao.getAllWishlistIds().map { it.toSet() }
    }

    override fun isWishlisted(productId: Int): Flow<Boolean> {
        return wishlistDao.isWishListed(productId)
    }

    override fun getWishlistItems(): Flow<PagingData<WishListItem>> {
        return Pager(
            config = PagingConfig(pageSize = 20, enablePlaceholders = false),
            pagingSourceFactory = {wishlistDao.getWishListItems()}
        ).flow.map { pagingData ->
            pagingData.map { entity -> entity.toDomain() }
        }
    }

    override suspend fun syncWishlist(userId: String) {
        TODO("Not yet implemented")
    }

    override fun getWishlistCount(): Flow<Int> {
        return wishlistDao.getWishlistedCount()
    }

    override suspend fun removeFromWishlist(productId: Int) {
        wishlistDao.removeFromWishList(productId)
    }
    override suspend fun deleteAllWishlist(){
        wishlistDao.deleteAllWishlist()
    }

}