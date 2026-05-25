package com.example.cartify.feature.home.data

import androidx.compose.ui.input.key.key
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.cartify.core.data.remote.api.CartifyApi
import com.example.cartify.core.data.remote.dto.ProductDto
import com.example.cartify.core.util.Constants
import retrofit2.HttpException
import java.io.IOException

class ProductPagingSource(
    private val api: CartifyApi,
    private val category: String?, // Now optional
    private val sortBy: String?,   // Added
    private val order: String?     // Added
) : PagingSource<Int, ProductDto>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductDto> {
        return try {
            val skip = params.key ?: 0

            // COMBINATION LOGIC:
            // If we have a category, call the category endpoint WITH sorting.
            // If we don't have a category (but we are here because of sortBy),
            // call the 'all products' endpoint WITH sorting.
            val response = if (category != null) {
                api.getProductsByCategory(category, params.loadSize, skip, sortBy, order)
            } else {
                api.getAllProducts(params.loadSize, skip, sortBy, order)
            }

            val products = response.products
            val endOfPagination = products.size + skip >= response.total
            // ... return LoadResult.Page as usual
            LoadResult.Page(
                data = products,
                prevKey = if(skip == 0) null else skip - Constants.PRODUCTS_PER_PAGE,
                nextKey = if(endOfPagination) null else skip + Constants.PRODUCTS_PER_PAGE
            )
        }catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }


    }

    override fun getRefreshKey(state: PagingState<Int, ProductDto>): Int? {
        // 1. Get the position of the item the user was looking at
        return state.anchorPosition?.let { anchorPosition ->
//            // 2. Find the page closest to that item
            val anchorPage = state.closestPageToPosition(anchorPosition)
//
//            // 3. Try to use the previous page's next key,
//            // or the next page's prev key to "re-center" the list
            anchorPage?.prevKey?.plus(Constants.PRODUCTS_PER_PAGE)
                ?: anchorPage?.nextKey?.minus(Constants.PRODUCTS_PER_PAGE)
        }
    }
}