package com.example.cartify.feature.wishlist.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.cartify.core.domain.model.CartItem
import com.example.cartify.core.domain.model.WishListItem
import com.example.cartify.feature.cart.data.repository.CartRepository
import com.example.cartify.feature.wishlist.data.repository.WishlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val wishlistRepository: WishlistRepository,
    private val cartRepository: CartRepository
): ViewModel() {

    // PagingData stays separate as requested
    val products: StateFlow<PagingData<WishListItem>> = wishlistRepository.getWishlistItems()
        .cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PagingData.empty()
        )

    private val _error = MutableStateFlow<String?>(null)

    // uiState handles all non-paging state (count and errors)
    val uiState: StateFlow<WishlistUiState> = combine(
        wishlistRepository.getWishlistCount(),
        _error
    ) { count, error ->
        WishlistUiState(
            count = count,
            error = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = WishlistUiState()
    )

    fun deleteWishlistItem(productId: Int) {
        viewModelScope.launch {
            try {
                wishlistRepository.removeFromWishlist(productId = productId)
            } catch (e: Exception) {
                _error.update { "Failed to remove item: ${e.message}" }
            }
        }
    }

    fun deleteAllWishlist() {
        viewModelScope.launch {
            try {
                wishlistRepository.deleteAllWishlist()
            } catch (e: Exception) {
                _error.update { "Failed to clear wishlist: ${e.message}" }
            }
        }
    }

    fun onErrorMessageShown() {
        _error.update { null }
    }

    fun addToCart(product: WishListItem) {
        viewModelScope.launch{
            cartRepository.addWishlistItemToCart(product)
        }
    }

    init {
        syncWishlist()
    }
    private fun syncWishlist() {
        viewModelScope.launch {
            wishlistRepository.syncWishlistFromFirestore()
        }
    }
}

data class WishlistUiState(
    val count: Int = 0,
    val error: String? = null
)
