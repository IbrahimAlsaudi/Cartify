package com.example.cartify.core.data

import com.example.cartify.core.data.local.entity.CartItemEntity
import com.example.cartify.core.data.local.entity.ProductEntity
import com.example.cartify.core.data.local.entity.WishlistItemEntity
import com.example.cartify.core.data.remote.dto.CategoryDto
import com.example.cartify.core.data.remote.dto.ProductDto
import com.example.cartify.core.domain.model.CartItem
import com.example.cartify.core.domain.model.Category
import com.example.cartify.core.domain.model.Product
import com.example.cartify.core.domain.model.WishListItem
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json


fun Product.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        title = title,
        description = description,
        price = price,
        discountPercentage = discountPercentage,
        rating = rating,
        stock = stock,
        brand = brand,
        category = category,
        thumbnail = thumbnail,
        images = Json.encodeToString(images)
    )
}
fun ProductDto.toEntity(): ProductEntity {
    return ProductEntity(
        id = id,
        title = title,
        description = description,
        price = price,
        discountPercentage = discountPercentage,
        rating = rating,
        stock = stock,
        brand = brand,
        category = category,
        thumbnail = thumbnail,
        images = Json.encodeToString(images)
    )
}

fun ProductEntity.toDomain(): Product {
    return Product(
        id = id,
        title = title,
        description = description,
        price = price,
        discountPercentage = discountPercentage,
        rating = rating,
        stock = stock,
        brand = brand,
        category = category,
        images = Json.decodeFromString(images),
        thumbnail = thumbnail,
    )
}

fun ProductDto.toDomain(): Product {
    return Product(
        id = id,
        title = title,
        description = description,
        price = price,
        discountPercentage = discountPercentage,
        rating = rating,
        stock = stock,
        brand = brand,
        category = category,
        thumbnail = thumbnail,
        images = images,
    )
}

fun CategoryDto.toDomain(): Category {
    return Category(
        slug = slug,
        name = name
    )
}

fun Product.toWishlistEntity(): WishlistItemEntity {
    return WishlistItemEntity(
        productId = id,
        title = title,
        price = price,
        thumbnail = thumbnail,
        addedAt = System.currentTimeMillis()
    )
}

fun WishlistItemEntity.toDomain(): WishListItem {
    return WishListItem(
        productId = productId,
        title = title,
        price = price,
        thumbnail = thumbnail,
        addedAt = addedAt
    )
}

fun CartItemEntity.toDomain(): CartItem {
    return CartItem(
        id = id,
        productId = productId,
        title = title,
        price = price,
        thumbnail = thumbnail,
        quantity = quantity,
        addedAt = addedAt
    )
}

fun CartItem.toEntity(): CartItemEntity {
    return CartItemEntity(
        id = id,
        productId = productId,
        title = title,
        price = price,
        thumbnail = thumbnail,
        quantity = quantity,
        addedAt = addedAt
    )
}

fun Product.toCartItemEntity(): CartItemEntity {
    return CartItemEntity(
        productId = id,
        title = title,
        price = price,
        thumbnail = thumbnail,
        quantity = 1, /*Insert the cart item with default quantity of 1*/
        addedAt = System.currentTimeMillis()
    )
}

fun WishListItem.toCartItemEntity(): CartItemEntity {
    return CartItemEntity(
        productId = productId,
        title = title,
        price = price,
        thumbnail = thumbnail,
        quantity = 1, /*Insert the cart item with default quantity of 1*/
        addedAt = System.currentTimeMillis()
    )
}