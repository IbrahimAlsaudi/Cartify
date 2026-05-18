package com.example.cartify.feature.search.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.example.cartify.core.data.remote.api.CartifyApi
import com.example.cartify.core.data.remote.dto.ProductDto
import com.example.cartify.core.util.Constants
import retrofit2.HttpException
import java.io.IOException

class SearchPagingSource(
    private val api: CartifyApi,
    private val query: String
): PagingSource<Int, ProductDto>() {
    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProductDto> {
        return try {
            val skip = params.key ?: 0
            val response = api.searchProduct(
                query = query,
                limit = params.loadSize,
                skip = skip
            )

            val products = response.products
            val endOfPagination = products.size + skip >= response.total

            LoadResult.Page(
                data = products,
                prevKey = if(skip == 0) null else skip - Constants.PRODUCTS_PER_PAGE,
                nextKey = if (endOfPagination) null else skip + Constants.PRODUCTS_PER_PAGE
            )
        } catch (e: IOException) {
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