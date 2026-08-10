package com.example.cartify.feature.cart.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cartify.core.data.remote.dto.paymob.IntentionBillingDataDto
import com.example.cartify.core.data.remote.dto.paymob.IntentionItemDto
import com.example.cartify.core.domain.model.CartItem
import com.example.cartify.feature.auth.data.repository.AuthRepository
import com.example.cartify.feature.cart.data.repository.CartRepository
import com.example.cartify.feature.checkout.data.repository.PaymobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
    private val cartRepository: CartRepository,
    private val authRepository: AuthRepository,
    private val paymobRepository: PaymobRepository
) : ViewModel() {

    val uiState = cartRepository.getCartItems()
        .map { products ->
            CartUiState(
                products = products,
                productCount = products.size,
                total = products.sumOf { it.price * it.quantity },
                isLoading = false,
                error = null,
                isAnonymous = authRepository.isAnonymous()
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

    private val _paymentState = MutableStateFlow<PaymentState>(PaymentState.Idle)
    val paymentState: StateFlow<PaymentState> = _paymentState.asStateFlow()

    fun increaseCount(productId: Int) {
        viewModelScope.launch {
            cartRepository.increaseQuantity(productId)
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

    fun proceedToCheckout() {
        viewModelScope.launch {
            val currentState = uiState.value

            if (currentState.isAnonymous == true) {
                _paymentState.value = PaymentState.Error("Please sign in to place an order.")
                return@launch
            }

            if (currentState.products.isEmpty()) {
                _paymentState.value = PaymentState.Error("Your cart is empty.")
                return@launch
            }

            _paymentState.value = PaymentState.Loading

            val user = authRepository.getCurrentUser()

            val items = currentState.products.map { cartItem ->
                IntentionItemDto(
                    name = cartItem.title,
                    amountCents = (cartItem.price * cartItem.quantity * 100).toInt(),
//                    quantity = cartItem.quantity
                )
            }

            val billingData = IntentionBillingDataDto(
                firstName = user?.name?.split(" ")?.firstOrNull() ?: "NA",
                lastName = user?.name?.split(" ")?.lastOrNull() ?: "NA",
                email = user?.email ?: "NA",
//                phoneNumber = "+201000000000" // until phone is added to profile
            )

            val amountCents = (currentState.total * 100).toInt()

            paymobRepository.createIntention(
                amountCents = amountCents,
                items = items,
                billingData = billingData
            ).fold(
                onSuccess = { clientSecret ->
                    _paymentState.value = PaymentState.ReadyToPay(clientSecret)
                },
                onFailure = { e ->
                    _paymentState.value = PaymentState.Error(e.message ?: "Payment failed")
                }
            )
        }
    }

    fun onPaymentSuccess() {
        // order creation goes here next
        _paymentState.value = PaymentState.Idle
    }

    fun onPaymentFailure() {
        _paymentState.value = PaymentState.Error("Payment was unsuccessful, please try again.")
    }

    fun resetPaymentState() {
        _paymentState.value = PaymentState.Idle
    }
}

data class CartUiState(
    val products: List<CartItem> = emptyList(),
    val productCount: Int = 0,
    val total: Double = 0.0,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isAnonymous: Boolean? = null
)

sealed interface PaymentState {
    data object Idle : PaymentState
    data object Loading : PaymentState
    data class ReadyToPay(val clientSecret: String) : PaymentState
    data class Error(val message: String) : PaymentState
}