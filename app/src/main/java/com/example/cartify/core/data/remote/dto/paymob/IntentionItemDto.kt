package com.example.cartify.core.data.remote.dto.paymob

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IntentionItemDto(
    @SerialName("name")     val name: String,
    @SerialName("amount")   val amountCents: Int,
)
