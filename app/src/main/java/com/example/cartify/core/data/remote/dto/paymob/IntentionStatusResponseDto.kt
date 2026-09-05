package com.example.cartify.core.data.remote.dto.paymob

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IntentionStatusResponseDto(
    @SerialName("confirmed") val confirmed: Boolean,
    @SerialName("status") val status: String
)
