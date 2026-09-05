package com.example.cartify.core.data.firebase

import com.example.cartify.core.data.local.entity.CartItemEntity
import com.example.cartify.core.data.local.entity.OrderEntity
import com.example.cartify.core.data.local.entity.OrderItemEntity
import com.example.cartify.core.data.local.entity.WishlistItemEntity
import com.example.cartify.core.domain.model.Order
import com.example.cartify.core.domain.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirestoreSource @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    // ── User ──────────────────────────────────────────────────────────────

    suspend fun createUserDocument(user: User) {
        firestore.collection("users")
            .document(user.id)
            .set(mapOf(
                "id" to user.id,
                "name" to user.name,
                "email" to user.email,
                "profilePicture" to user.profilePicture,
                "createdAt" to System.currentTimeMillis()
            )).await()
    }

    suspend fun getUserDocument(userId: String): User? {
        val snapshot = firestore.collection("users")
            .document(userId)
            .get().await()
        return if (snapshot.exists()) {
            User(
                id = snapshot.getString("id") ?: "",
                name = snapshot.getString("name") ?: "",
                email = snapshot.getString("email") ?: "",
                profilePicture = snapshot.getString("profilePicture"),
                isAnonymous = false
            )
        } else null
    }

    suspend fun updateUserDocument(userId: String, name: String, profilePicture: String?) {
        firestore.collection("users")
            .document(userId)
            .update(mapOf(
                "name" to name,
                "profilePicture" to profilePicture
            )).await()
    }

    // ── Cart ──────────────────────────────────────────────────────────────

    suspend fun addToCart(userId: String, cartItem: CartItemEntity) {
        firestore.collection("users")
            .document(userId)
            .collection("cart")
            .document(cartItem.productId.toString())
            .set(mapOf(
                "productId" to cartItem.productId,
                "title" to cartItem.title,
                "price" to cartItem.price,
                "thumbnail" to cartItem.thumbnail,
                "quantity" to cartItem.quantity,
                "addedAt" to cartItem.addedAt
            )).await()
    }

    suspend fun removeFromCart(userId: String, productId: Int) {
        firestore.collection("users")
            .document(userId)
            .collection("cart")
            .document(productId.toString())
            .delete().await()
    }

    suspend fun updateCartQuantity(userId: String, productId: Int, quantity: Int) {
        firestore.collection("users")
            .document(userId)
            .collection("cart")
            .document(productId.toString())
            .update("quantity", quantity).await()
    }

    suspend fun clearCart(userId: String) {
        val batch = firestore.batch()
        val documents = firestore.collection("users")
            .document(userId)
            .collection("cart")
            .get().await()
        documents.forEach { batch.delete(it.reference) }
        batch.commit().await()
    }

    suspend fun getCart(userId: String): List<CartItemEntity> {
        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("cart")
            .get().await()
        return snapshot.documents.map { doc ->
            CartItemEntity(
                productId = (doc.getLong("productId") ?: 0).toInt(),
                title = doc.getString("title") ?: "",
                price = doc.getDouble("price") ?: 0.0,
                thumbnail = doc.getString("thumbnail") ?: "",
                quantity = (doc.getLong("quantity") ?: 1).toInt(),
                addedAt = doc.getLong("addedAt") ?: System.currentTimeMillis()
            )
        }
    }

    // ── Wishlist ──────────────────────────────────────────────────────────

    suspend fun addToWishlist(userId: String, item: WishlistItemEntity) {
        firestore.collection("users")
            .document(userId)
            .collection("wishlist")
            .document(item.productId.toString())
            .set(mapOf(
                "productId" to item.productId,
                "title" to item.title,
                "price" to item.price,
                "thumbnail" to item.thumbnail,
                "addedAt" to item.addedAt
            )).await()
    }

    suspend fun removeFromWishlist(userId: String, productId: Int) {
        firestore.collection("users")
            .document(userId)
            .collection("wishlist")
            .document(productId.toString())
            .delete().await()
    }

    suspend fun getWishlist(userId: String): List<WishlistItemEntity> {
        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("wishlist")
            .get().await()
        return snapshot.documents.map { doc ->
            WishlistItemEntity(
                productId = (doc.getLong("productId") ?: 0).toInt(),
                title = doc.getString("title") ?: "",
                price = doc.getDouble("price") ?: 0.0,
                thumbnail = doc.getString("thumbnail") ?: "",
                addedAt = doc.getLong("addedAt") ?: System.currentTimeMillis()
            )
        }
    }

    suspend fun clearWishlist(userId: String) {
        val batch = firestore.batch()
        val documents = firestore.collection("users")
            .document(userId)
            .collection("wishlist")
            .get().await()
        documents.forEach { batch.delete(it.reference) }
        batch.commit().await()
    }

    // ── Orders ────────────────────────────────────────────────────────────


    suspend fun createOrder(userId: String, order: OrderEntity, items: List<OrderItemEntity>) {
        val orderRef = firestore.collection("users")
            .document(userId)
            .collection("orders")
            .document(order.id)

        val cartRef = firestore.collection("users")
            .document(userId)
            .collection("cart")

        val batch = firestore.batch()

        batch.set(orderRef, mapOf(
            "id"             to order.id,
            "totalPrice"     to order.totalPrice,
            "status"         to order.status,
            "createdAt"      to order.createdAt,
//            "deliveryAddress" to order.deliveryAddress,
            "paymentMethod"  to order.paymentMethod,
            "paymobOrderId"  to order.paymobOrderId
        ))

        items.forEach { item ->
            val itemRef = orderRef.collection("items").document()
            batch.set(itemRef, mapOf(
                "productId" to item.productId,
                "title"     to item.title,
                "price"     to item.price,
                "thumbnail" to item.thumbnail,
                "quantity"  to item.quantity
            ))
        }

        // clear cart in the same batch — either everything succeeds or nothing does
        val cartDocs = cartRef.get().await()
        cartDocs.forEach { doc ->
            batch.delete(doc.reference)
        }

        batch.commit().await()
    }

    suspend fun updateOrderStatus(userId: String, orderId: String, status: String) {
        firestore.collection("users")
            .document(userId)
            .collection("orders")
            .document(orderId)
            .update("status", status).await()
    }

    suspend fun getOrders(userId: String): List<OrderEntity> {
        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("orders")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .get().await()

        return snapshot.documents.map { doc ->
            OrderEntity(
                id = doc.getString("id") ?: "",
                userId = userId,
                totalPrice = doc.getDouble("totalPrice") ?: 0.0,
                status = doc.getString("status") ?: "PENDING",
                createdAt = doc.getLong("createdAt") ?: 0L,
//                deliveryAddress = doc.getString("deliveryAddress") ?: "",
                paymentMethod = doc.getString("paymentMethod") ?: "",
                paymobOrderId = doc.getLong("paymobOrderId") ?: 0L
            )
        }
    }

    suspend fun getOrderItems(userId: String, orderId: String): List<OrderItemEntity> {
        val snapshot = firestore.collection("users")
            .document(userId)
            .collection("orders")
            .document(orderId)
            .collection("items")
            .get().await()

        return snapshot.documents.map { doc ->
            OrderItemEntity(
                orderId = orderId,
                productId = (doc.getLong("productId") ?: 0).toInt(),
                title = doc.getString("title") ?: "",
                price = doc.getDouble("price") ?: 0.0,
                thumbnail = doc.getString("thumbnail") ?: "",
                quantity = (doc.getLong("quantity") ?: 1).toInt()
            )
        }
    }

    fun observeOrderStatus(userId: String, orderId: String): Flow<String?> = callbackFlow {
        val listener = firestore.collection("users")
            .document(userId)
            .collection("orders")
            .document(orderId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.getString("status"))
            }
        awaitClose { listener.remove() }
    }

    // ── Delete User ───────────────────────────────────────────────────────

    suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            val userRef = firestore.collection("users").document(userId)

            // Delete cart
            val cartBatch = firestore.batch()
            val cartDocs = userRef.collection("cart").get().await()
            cartDocs.forEach { cartBatch.delete(it.reference) }
            cartBatch.commit().await()

            // Delete wishlist
            val wishlistBatch = firestore.batch()
            val wishlistDocs = userRef.collection("wishlist").get().await()
            wishlistDocs.forEach { wishlistBatch.delete(it.reference) }
            wishlistBatch.commit().await()

            // Delete orders and their items
            val orderDocs = userRef.collection("orders").get().await()
            orderDocs.forEach { orderDoc ->
                val itemsBatch = firestore.batch()
                val itemDocs = orderDoc.reference.collection("items").get().await()
                itemDocs.forEach { itemsBatch.delete(it.reference) }
                itemsBatch.commit().await()
                orderDoc.reference.delete().await()
            }

            // Delete user document
            userRef.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}