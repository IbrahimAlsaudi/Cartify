package com.example.cartify.feature.checkout.data.repository

import com.example.cartify.BuildConfig
import com.example.cartify.core.data.remote.api.PaymobApi
import com.example.cartify.core.data.remote.dto.paymob.IntentionBillingDataDto
import com.example.cartify.core.data.remote.dto.paymob.IntentionItemDto
import com.example.cartify.core.data.remote.dto.paymob.IntentionRequestDto
import javax.inject.Inject

interface PaymobRepository {
    suspend fun createIntention(
        amountCents: Int,
        items: List<IntentionItemDto>,
        billingData: IntentionBillingDataDto
    ): Result<String>
}

class PaymobRepositoryImpl @Inject constructor(
    private val paymobApi: PaymobApi
) : PaymobRepository {

    override suspend fun createIntention(
        amountCents: Int,
        items: List<IntentionItemDto>,
        billingData: IntentionBillingDataDto
    ): Result<String> {
        return try {
            val response = paymobApi.createIntention(
                authorization = "Token ${BuildConfig.PAYMOB_SECRET_KEY}",
                request = IntentionRequestDto(
                    amount = amountCents,
                    paymentMethods = listOf(BuildConfig.PAYMOB_INTEGRATION_ID.toInt()),
                    items = items,
                    billingData = billingData
                )
            )
            Result.success(response.clientSecret)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}