package com.example.cartify.core.data.remote.api

import com.example.cartify.core.data.remote.dto.paymob.IntentionRequestDto
import com.example.cartify.core.data.remote.dto.paymob.IntentionResponseDto
import com.example.cartify.core.data.remote.dto.paymob.IntentionStatusResponseDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path

interface PaymobApi {
    @POST("v1/intention/")
    suspend fun createIntention(
        @Header("Authorization") authorization: String,
        @Body request: IntentionRequestDto
    ): IntentionResponseDto

    @GET("v1/intention/{intentionId}/")
    suspend fun getIntention(
        @Header("Authorization") authorization: String,
        @Path("intentionId") intentionId: String
    ): IntentionStatusResponseDto

    @GET("v1/intention/element/{publicKey}/{clientSecret}/")
    suspend fun getIntentionStatus(
        @Path("publicKey")    publicKey: String,
        @Path("clientSecret") clientSecret: String
    ): IntentionStatusResponseDto
}