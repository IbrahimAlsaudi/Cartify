package com.example.cartify.core.domain.model

enum class ProductSortOrder(val displayName: String, val sortBy: String?, val order: String?) {
    DEFAULT("Default", null, null),
    PRICE_LOW_TO_HIGH("Price: Low to High", "price", "asc"),
    PRICE_HIGH_TO_LOW("Price: High to Low", "price", "desc"),
    RATING_HIGH_TO_LOW("Rating: High to Low", "rating", "desc"),
    TITLE_ASC("Name: A to Z", "title", "asc"),
    TITLE_DESC("Name: Z to A", "title", "desc")
}