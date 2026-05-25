package com.example.cartify.feature.wishlist.data.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.cartify.core.data.firebase.FirestoreSource
import com.example.cartify.core.data.local.dao.WishlistDao
import com.example.cartify.core.data.toDomain
import com.example.cartify.core.data.toWishlistEntity
import com.example.cartify.core.domain.model.Product
import com.example.cartify.core.domain.model.WishListItem
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

interface WishlistRepository {
    suspend fun toggleWishlist(product: Product): Result<Unit>
    fun getWishlistedIds(): Flow<Set<Int>>
    fun isWishlisted(productId: Int): Flow<Boolean>
    fun getWishlistItems(): Flow<PagingData<WishListItem>>
    suspend fun syncWishlistFromFirestore(): Result<Unit>
    fun getWishlistCount(): Flow<Int>
    suspend fun removeFromWishlist(productId: Int): Result<Unit>

    suspend fun deleteAllWishlist(): Result<Unit>

    suspend fun mergeLocalDataWithCloud(): Result<Unit>
}

class WishlistRepositoryImpl @Inject constructor(
    private val wishlistDao: WishlistDao,
    private val firestoreSource: FirestoreSource,
    private val auth: FirebaseAuth
): WishlistRepository {

    private val userId get() = auth.currentUser?.uid

    override suspend fun toggleWishlist(product: Product): Result<Unit> {
        return try {
            if(wishlistDao.isWishlistedOnce(product.id)) {
                wishlistDao.removeFromWishList(product.id)
                userId?.let { uid ->
                    firestoreSource.removeFromWishlist(uid,product.id)
                }
            } else {
                wishlistDao.addToWishList(product.toWishlistEntity())
                userId?.let { uid ->
                    firestoreSource.addToWishlist(uid,product.toWishlistEntity())
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.d("WishlistRepository: ", e.message, e)
            Result.failure(e)
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
            pagingSourceFactory = wishlistDao::getWishListItems
        ).flow.map { pagingData ->
            pagingData.map { entity -> entity.toDomain() }
        }
    }

    override suspend fun syncWishlistFromFirestore(): Result<Unit> {
        return try {
            val uid = userId ?: return Result.success(Unit)
            val cloudItems = firestoreSource.getWishlist(uid)
            wishlistDao.deleteAllWishlist()
            cloudItems.forEach { wishlistDao.addToWishList(it) }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.d("WishlistRepository: ", e.message, e)
            Result.failure(e)
        }
    }

    override fun getWishlistCount(): Flow<Int> {
        return wishlistDao.getWishlistedCount()
    }

    override suspend fun removeFromWishlist(productId: Int): Result<Unit> {
        return try {
        wishlistDao.removeFromWishList(productId)
           userId?.let { uid ->
               firestoreSource.removeFromWishlist(uid, productId)
           }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.d("WishlistRepository: ", e.message, e)
            Result.failure(e)
        }

    }
    override suspend fun deleteAllWishlist():Result<Unit>{
        return try {
            wishlistDao.deleteAllWishlist()
            userId?.let { uid ->
                firestoreSource.clearWishlist(uid)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.d("WishlistRepository: ", e.message, e)
            Result.failure(e)
        }
    }

    override suspend fun mergeLocalDataWithCloud(): Result<Unit> {
        return try {
            val uid = userId ?: return Result.success(Unit)
            val localItems = wishlistDao.getAllWishlistItems()
            localItems.forEach { item ->
                firestoreSource.addToWishlist(uid, item)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Log.d("WishlistRepository: ", e.message, e)
            Result.failure(e)
        }
    }

}