package com.example.cartify.core.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.cartify.core.data.local.entity.RemoteKeyEntity

@Dao
interface RemoteKeyDao {

    @Query("SELECT * FROM remote_keys WHERE productId = :productId")
    suspend fun getRemoteKey(productId: Int): RemoteKeyEntity?

    @Upsert
    suspend fun upsertRemoteKeys(remoteKeys: List<RemoteKeyEntity>)

    @Query("DELETE FROM remote_keys")
    suspend fun deleteAllRemoteKeys()

}