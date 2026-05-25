package com.example.cartify.feature.search.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.cartify.core.domain.model.Product
import com.example.cartify.feature.home.data.repository.ProductRepository
import com.example.cartify.feature.home.presentation.home.HomeUiState
import com.example.cartify.feature.wishlist.data.repository.WishlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val wishlistRepository: WishlistRepository
): ViewModel() {
    private val _products = MutableStateFlow<PagingData<Product>>(PagingData.empty())
    val products = _products.cachedIn(viewModelScope).stateIn(scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = PagingData.empty())


    private val _searchQuery = MutableStateFlow<String>("")

    private val wishlistedIds = wishlistRepository.getWishlistedIds()

    val uiState: StateFlow<SearchUiState> = combine(
        _searchQuery, wishlistedIds
    ) { query, wishlistIds ->
        SearchUiState(
            query = query,
            wishlistIds = wishlistIds
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = SearchUiState()
    )
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun searchProducts(query: String) {
        viewModelScope.launch {
            productRepository.searchProduct(query).cachedIn(viewModelScope).collect {
                _products.value = it
            }

        }
    }



    fun clearSearchQuery() {
        _searchQuery.value = ""
    }

    fun onWishlistToggle(product: Product) {
        viewModelScope.launch {
            wishlistRepository.toggleWishlist(product)
        }
    }
}

data class SearchUiState(
    val query: String = "",
    val wishlistIds: Set<Int> = emptySet(),

)