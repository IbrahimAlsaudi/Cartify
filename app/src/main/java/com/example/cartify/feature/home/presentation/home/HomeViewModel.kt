package com.example.cartify.feature.home.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.cartify.core.domain.model.Category
import com.example.cartify.core.domain.model.Product
import com.example.cartify.feature.home.data.repository.ProductRepository
import com.example.cartify.feature.wishlist.data.repository.WishlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject






@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val wishlistRepository: WishlistRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()





    @OptIn(ExperimentalCoroutinesApi::class)
    val products: StateFlow<PagingData<Product>> = _uiState
        .map { it.selectedCategory }
        .distinctUntilChanged()
        .flatMapLatest { category ->

            if (category == null) productRepository.getProducts()
            else productRepository.getProductsByCategory(category.slug) /*Api call and not from room*/
        }
        .cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PagingData.empty()
        )
    fun onWishlistToggle(product: Product) {
        viewModelScope.launch {
            wishlistRepository.toggleWishlist(product)
        }
    }

    fun onCategorySelected(category: Category?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingCategories = true) }
            try {
                _uiState.update {
                    it.copy(
                        categories = productRepository.getCategories(),
                        isLoadingCategories = false,
                        categoriesError = null
                    )
                }
            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is IOException -> "No internet connection"
                    is HttpException -> "Server error: ${e.code()}"
                    else -> "An unexpected error occurred"
                }
                _uiState.update {
                    it.copy(
                        categoriesError = errorMessage,
                        isLoadingCategories = false
                    )
                }
            }
        }
    }

    init {
        loadCategories()
        observeWishlistedIds()
    }

    private fun observeWishlistedIds() {
        viewModelScope.launch {
            wishlistRepository.getWishlistedIds().collect { ids ->
                _uiState.update { it.copy(wishlistedIds = ids) }
            }
        }
    }

}

data class HomeUiState(
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null,
    val categoriesError: String? = null,
    val isLoadingCategories: Boolean = true,
    val wishlistedIds: Set<Int> = emptySet(),
)