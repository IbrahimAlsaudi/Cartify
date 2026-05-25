package com.example.cartify.feature.home.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.cartify.core.domain.model.Category
import com.example.cartify.core.domain.model.Product
import com.example.cartify.core.domain.model.ProductSortOrder
import com.example.cartify.feature.auth.data.repository.AuthRepository
import com.example.cartify.feature.home.data.repository.ProductRepository
import com.example.cartify.feature.wishlist.data.repository.WishlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject


private data class HomeFilters(
    val categorySlug: String?,
    val sortOrder: ProductSortOrder
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val wishlistRepository: WishlistRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val isAnonymous = authRepository.isAnonymous()
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val userName = if (isAnonymous) "Guest" else authRepository.getCurrentUser()?.name
    private fun setUserName() {
        _uiState.update { it.copy(userName = userName ?: "xxx") }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    val products: StateFlow<PagingData<Product>> = _uiState
        .map { HomeFilters(it.selectedCategory?.slug, it.selectedSort) }
        .distinctUntilChanged()
        .flatMapLatest { filters ->
            // Use a single repository function that handles the logic of 
            // switching between RemoteMediator and Network-only PagingSource.
            productRepository.getProducts(
                category = filters.categorySlug,
                sortBy = filters.sortOrder.sortBy,
                order = filters.sortOrder.order
            )
        }
        .cachedIn(viewModelScope)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = PagingData.empty()
        )

    fun onCategorySelected(category: Category?) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun onSortSelected(sort: ProductSortOrder) {
        _uiState.update { it.copy(selectedSort = sort) }
    }

    fun onWishlistToggle(product: Product) {
        viewModelScope.launch {
            wishlistRepository.toggleWishlist(product)
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoadingCategories = true) }
            try {
                val categories = productRepository.getCategories()
                _uiState.update {
                    it.copy(
                        categories = categories,
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

    private fun observeWishlistedIds() {
        viewModelScope.launch {
            wishlistRepository.getWishlistedIds().collect { ids ->
                _uiState.update { it.copy(wishlistedIds = ids) }
            }
        }
    }



    fun retryLoadCategories() {
        if (uiState.value.categories.isEmpty()) {
            loadCategories()
        }
    }

    init {
        setUserName()
        loadCategories()
        observeWishlistedIds()
    }
}

data class HomeUiState(
    val categories: List<Category> = emptyList(),
    val selectedCategory: Category? = null,
    val selectedSort: ProductSortOrder = ProductSortOrder.DEFAULT,
    val categoriesError: String? = null,
    val isLoadingCategories: Boolean = true,
    val wishlistedIds: Set<Int> = emptySet(),
    val userName: String = ""
)