package com.example.cartify.feature.cart.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cartify.core.domain.model.CartItem
import com.example.cartify.feature.cart.data.repository.CartRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
): ViewModel() {



    val uiState = cartRepository.getCartItems()
        .map { products ->
            CartUiState(
                products = products,
                productCount = products.size,
                total = products.sumOf { it.price * it.quantity },
                isLoading = false,
                error = null
            )
        }.catch { throwable ->
            emit(
                CartUiState(
                    isLoading = false,
                    error = throwable.message
                )
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CartUiState()
        )

    fun increaseCount(id: Int) {
        viewModelScope.launch {
            cartRepository.increaseQuantity(id)
        }
    }

    fun decreaseCount(id: Int) {
        viewModelScope.launch {
            cartRepository.decreaseQuantity(id)
        }
    }


    fun deleteItem(id: Int) {
        viewModelScope.launch {
            cartRepository.removeFromCart(id)
        }
    }
    
}

data class CartUiState(
    val products: List<CartItem> = emptyList(),
    val productCount: Int = 0,
    val total: Double = 0.0,
    val isLoading: Boolean = true,
    val error: String? = null
)