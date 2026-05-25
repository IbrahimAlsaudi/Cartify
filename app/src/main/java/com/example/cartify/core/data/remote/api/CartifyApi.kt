package com.example.cartify.core.data.remote.api

import com.example.cartify.core.data.remote.dto.CategoryDto
import com.example.cartify.core.data.remote.dto.ProductDto
import com.example.cartify.core.data.remote.dto.ProductResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CartifyApi {
    @GET("products")
    suspend fun getAllProducts(
        @Query("limit") limit: Int,
        @Query("skip") skip: Int,
        @Query("sortBy") sortBy: String?,
        @Query("order") order: String?
    ): ProductResponseDto


    @GET("products/category/{category}")
    suspend fun getProductsByCategory(
        @Path("category") category: String,
        @Query("limit") limit: Int,
        @Query("skip") skip: Int,
        @Query("sortBy") sortBy: String?,
        @Query("order") order: String?
    ): ProductResponseDto



    @GET("products/categories")
    suspend fun getCategories(): List<CategoryDto>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): ProductDto

    @GET("products/search")
    suspend fun searchProduct(
        @Query("q") query: String,
        @Query("limit") limit: Int,
        @Query("skip") skip: Int
    ): ProductResponseDto

}