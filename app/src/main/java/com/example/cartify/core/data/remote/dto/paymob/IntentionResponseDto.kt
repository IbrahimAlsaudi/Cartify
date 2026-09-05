package com.example.cartify.core.data.remote.dto.paymob

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IntentionResponseDto(
    @SerialName("client_secret")        val clientSecret: String,
    @SerialName("intention_order_id")   val intentionOrderId: Long,
    @SerialName("id") val id: String
)
