package com.example.cartify.core.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.cartify.core.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products")
    fun getProducts(): PagingSource<Int, ProductEntity> /*For Pagination only load as user scroll, Do not load all the products at once the user is not using a super computer*/

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Int): ProductEntity? /*the user maybe watching product details from a category list which is not cached for the database so maybe it returns null*/
    @Upsert
    suspend fun upsertProduct(products: List<ProductEntity>) /*To save from the api to room*/

    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()

    @Query("""
        SELECT stock FROM products
        WHERE id = :productId
    """)
    suspend fun getProductStock(productId: Int): Int /*Suspend and not flow because the quantity does not get changed in the backend so it is a one-time call*/
}