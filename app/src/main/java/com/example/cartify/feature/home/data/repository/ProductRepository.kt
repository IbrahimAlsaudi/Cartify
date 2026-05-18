package com.example.cartify.feature.home.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingSourceFactory
import androidx.paging.map
import com.example.cartify.core.data.local.CartifyDatabase
import com.example.cartify.core.data.local.entity.ProductEntity
import com.example.cartify.core.data.remote.api.CartifyApi
import com.example.cartify.core.data.remote.dto.CategoryDto
import com.example.cartify.core.data.toDomain
import com.example.cartify.core.data.toEntity
import com.example.cartify.core.domain.model.Category
import com.example.cartify.core.domain.model.Product
import com.example.cartify.core.util.Constants
import com.example.cartify.feature.home.data.CategoryPagingSource
import com.example.cartify.feature.home.data.mediator.ProductRemoteMediator
import com.example.cartify.feature.search.data.SearchPagingSource
import com.example.cartify.feature.wishlist.data.repository.WishlistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import javax.inject.Inject


interface ProductRepository {
    fun getProducts(): Flow<PagingData<Product>>
    fun getProductsByCategory(slug: String): Flow<PagingData<Product>>
    suspend fun getCategories(): List<Category>
    suspend fun getProductById(id: Int): Product
    suspend fun searchProduct(query: String): Flow<PagingData<Product>>
}

class ProductRepositoryImpl @Inject constructor(
    private val database: CartifyDatabase,
    private val api: CartifyApi,
): ProductRepository {

    // 1. We create a reusable Flow for Wishlist IDs to avoid duplication
    private val wishlistIdsFlow = database.wishlistDao().getAllWishlistIds().map { it.toSet() }
    @OptIn(ExperimentalPagingApi::class)
    override fun getProducts(): Flow<PagingData<Product>> {
       return Pager(
           config = PagingConfig(
               pageSize = Constants.PRODUCTS_PER_PAGE,
               prefetchDistance = 5,
               enablePlaceholders = false
           ),
           remoteMediator = ProductRemoteMediator(api = api, database = database),
           pagingSourceFactory = {database.productDao().getProducts()}
       ).flow.map { pagingData ->
           pagingData.map { entity -> entity.toDomain() }
       }

    }

    override fun getProductsByCategory(slug: String): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = Constants.PRODUCTS_PER_PAGE,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { CategoryPagingSource(api = api, slug = slug) }
        ).flow.map { pagingData -> pagingData.map { dto -> dto.toDomain() } }
    }

    override suspend fun getCategories(): List<Category> {
        return api.getCategories().map { it.toDomain() }
    }

    override suspend fun getProductById(id: Int): Product {
        return database.productDao().getProductById(id)?.toDomain()
            ?: api.getProductById(id).toDomain()

    }

    override suspend fun searchProduct(query: String): Flow<PagingData<Product>> {
        return Pager(
            config = PagingConfig(
                pageSize = Constants.PRODUCTS_PER_PAGE,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { SearchPagingSource(api = api, query = query)}
        ).flow.map { pagingData ->  pagingData.map { dto -> dto.toDomain() }}
    }


}