package com.example.cartify.core.data.remote.api

import com.example.cartify.core.data.remote.dto.paymob.IntentionRequestDto
import com.example.cartify.core.data.remote.dto.paymob.IntentionResponseDto
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

interface PaymobApi {
    @POST("v1/intention/")
    suspend fun createIntention(
        @Header("Authorization") authorization: String,
        @Body request: IntentionRequestDto
    ): IntentionResponseDto
}