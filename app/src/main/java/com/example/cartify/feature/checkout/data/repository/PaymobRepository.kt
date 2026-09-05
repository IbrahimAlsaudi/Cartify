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
    ): Result<Triple<String, Long, String>>

    suspend fun getIntentionStatus(clientSecret: String): Result<Boolean>
}

class PaymobRepositoryImpl @Inject constructor(
    private val paymobApi: PaymobApi
) : PaymobRepository {

    override suspend fun createIntention(
        amountCents: Int,
        items: List<IntentionItemDto>,
        billingData: IntentionBillingDataDto
    ): Result<Triple<String, Long, String>> {
        return try {
            val response = paymobApi.createIntention(
                authorization = "Token ${BuildConfig.PAYMOB_SECRET_KEY}",
                request = IntentionRequestDto(
                    amount = amountCents,
                    currency = "EGP",
                    paymentMethods = listOf(BuildConfig.PAYMOB_INTEGRATION_ID.toInt()),
                    items = items,
                    billingData = billingData
                )
            )
            Result.success(Triple(response.clientSecret, response.intentionOrderId,response.id))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getIntentionStatus(clientSecret: String): Result<Boolean> {
        return try {
            val response = paymobApi.getIntentionStatus(
                publicKey    = BuildConfig.PAYMOB_PUBLIC_KEY,
                clientSecret = clientSecret
            )
            Result.success(response.confirmed)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}