package com.example.cartify.core.data.remote.dto.paymob

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class IntentionBillingDataDto(
    @SerialName("first_name")   val firstName: String,
    @SerialName("last_name")    val lastName: String,
    @SerialName("email")        val email: String,
)
