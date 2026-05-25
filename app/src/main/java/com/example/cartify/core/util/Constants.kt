package com.example.cartify.core.util

import androidx.compose.ui.graphics.Color
import com.example.cartify.BuildConfig
import com.example.cartify.core.domain.model.Banner

object Constants {
    const val BASE_URL = "https://dummyjson.com/"
    const val PRODUCTS_PER_PAGE = 20

    val BANNERS = listOf(
        Banner(
            title = "Flash Sale",
            subtitle = "Up to 50% off on Electronics",
            imageUrl = "https://images.unsplash.com/photo-1607082348824-0a96f2a4b9da?w=800"
        ),
        Banner(
            title = "Free Shipping",
            subtitle = "On all orders over \$50",
            imageUrl = "https://images.unsplash.com/photo-1586528116311-ad8dd3c8310d?w=800"
        ),
        Banner(
            title = "New Arrivals",
            subtitle = "Fresh styles just landed",
            imageUrl = "https://images.unsplash.com/photo-1483985988355-763728e1935b?w=800"
        )
    )

    const val WEB_CLIENT_ID = BuildConfig.WEB_CLIENT_ID
}