package com.example.cartify.core.data.remote.dto.paymob

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IntentionRequestDto(
    @SerialName("amount") val amount: Int,
    @SerialName("currency") val currency: String = "EGP",
    @SerialName("payment_methods") val paymentMethods: List<Int>,
    @SerialName("items") val items: List<IntentionItemDto>,
    @SerialName("billing_data") val billingData: IntentionBillingDataDto
)