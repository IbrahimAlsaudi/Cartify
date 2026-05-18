package com.example.cartify.feature.home.data.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import retrofit2.HttpException
import com.example.cartify.core.data.local.CartifyDatabase
import com.example.cartify.core.data.local.entity.ProductEntity
import com.example.cartify.core.data.local.entity.RemoteKeyEntity
import com.example.cartify.core.data.remote.api.CartifyApi
import com.example.cartify.core.data.toEntity
import com.example.cartify.core.util.Constants
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class ProductRemoteMediator(
    private val api: CartifyApi,
    private val database: CartifyDatabase
): RemoteMediator<Int, ProductEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ProductEntity>
    ): MediatorResult {
        return try {
            val skip = when(loadType) {
                LoadType.REFRESH -> 0

                LoadType.PREPEND -> {
                    val firstItem = state.firstItemOrNull()
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    val remoteKey = database.remoteKeyDao().getRemoteKey(firstItem.id)
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                    remoteKey.prevSkip
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                        ?: return MediatorResult.Success(endOfPaginationReached = true)

                    val remoteKey = database.remoteKeyDao().getRemoteKey(lastItem.id)
                        ?: return MediatorResult.Success(endOfPaginationReached = true)

                    remoteKey.nextSkip
                        ?: return MediatorResult.Success(endOfPaginationReached = true)
                }
            }

            val loadSize = if(loadType == LoadType.REFRESH) state.config.initialLoadSize else state.config.pageSize
            val response = api.getProducts(limit = loadSize, skip = skip)

            val products = response.products
            val endOfPaginationReached = skip + products.size >= response.total

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    database.remoteKeyDao().deleteAllRemoteKeys()
                    database.productDao().deleteAllProducts()
                }
                val prevSkip = if (skip == 0) null else skip - state.config.pageSize
                val nextSkip = if(endOfPaginationReached) null else skip + products.size

                val remoteKeys = products.map { product ->
                    RemoteKeyEntity(
                        productId = product.id,
                        prevSkip = prevSkip,
                        nextSkip = nextSkip
                    )
                }

                database.remoteKeyDao().upsertRemoteKeys(remoteKeys)
                database.productDao().upsertProduct(products.map { it.toEntity() })
            }
            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)

        } catch (e: IOException) {
            MediatorResult.Error(e) /*No internet, connection timeout, device-side problem*/
        } catch (e: HttpException) { /*server returned 404, 500 etc., server side problem*/
            MediatorResult.Error(e)
        }
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
        // always fetch fresh data on app start
    }
}