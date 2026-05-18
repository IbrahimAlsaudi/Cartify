package com.example.cartify.feature.shared

import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.example.cartify.app.navigation.Screen
import com.example.cartify.core.domain.model.Product
import com.example.cartify.feature.cart.data.repository.CartRepository
import com.example.cartify.feature.home.data.repository.ProductRepository
import com.example.cartify.feature.wishlist.data.repository.WishlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val wishlistRepository: WishlistRepository,
    private val cartRepository: CartRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val productId = savedStateHandle.toRoute<Screen.ProductDetailScreen>().id

    private val _productState = MutableStateFlow(ProductDetailUiState(isLoading = true))

    private val _isWishlisted: StateFlow<Boolean> = wishlistRepository
        .isWishlisted(productId)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = false
        )

    val uiState: StateFlow<ProductDetailUiState> = combine(
        _productState,
        _isWishlisted
    ) { productState, isWishlisted ->
        productState.copy(isWishlisted = isWishlisted)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = ProductDetailUiState(isLoading = true)
    )

    init {
        loadProduct()
    }

    private fun loadProduct() { /*The product details will never change while the use is on the screen thats why getProductById is suspend function and not a flow*/
        viewModelScope.launch {

            try {
                val product = productRepository.getProductById(productId)
                _productState.update { it.copy(product = product, isLoading = false) }
            } catch (e: IOException) {
                _productState.update { it.copy(error = "No internet connection", isLoading = false) }
            } catch (e: HttpException) {
                _productState.update { it.copy(error = "Server error: ${e.code()}", isLoading = false) }
            }
        }
    }

    fun onWishlistToggle() {
        viewModelScope.launch {
            uiState.value.product?.let { product ->
                wishlistRepository.toggleWishlist(product)
            }
        }
    }

    fun addToCart(product: Product) {
        viewModelScope.launch {
            cartRepository.addProductToCart(product)
            Log.d("Cart","Product ${product.title} added to Cart")
        }
    }
}
data class ProductDetailUiState(
    val product: Product? = null,
    val isLoading: Boolean = true,
    val error: String? = null,
    val isWishlisted: Boolean = false
)