package com.example.cartify.feature.home.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.cartify.R
import com.example.cartify.core.util.Constants
import com.example.cartify.feature.home.presentation.components.BannerSection
import com.example.cartify.feature.home.presentation.components.CategorySection
import com.example.cartify.feature.home.presentation.components.ProductCard
import com.example.cartify.feature.home.presentation.components.ShimmerProductCard
import com.example.cartify.feature.home.presentation.components.SortSection

@Composable
fun HomeScreen(
    onProductClicked: (Int) -> Unit,
    viewModel: HomeViewModel,

) {
    val uiState by viewModel.uiState.collectAsState()
    val products = viewModel.products.collectAsLazyPagingItems()
    val loadState = products.loadState

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp, start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // --- HEADER SECTION ---
        item(span = { GridItemSpan(maxLineSpan) }) {
            HomeTopBar(
                userName = uiState.userName.trim()
                    .substringBefore(" ")
                    .replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase()
                        else it.toString()
                    },
            )
        }
        // --- BANNER SECTION ---
        item(span = { GridItemSpan(maxLineSpan) }) {
            BannerSection(
                banners = Constants.BANNERS,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // --- CATEGORIES SECTION ---
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column {
                Text(
                    text = "Categories",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
                
                CategorySection(
                    categories = uiState.categories,
                    selectedCategory = uiState.selectedCategory,
                    isLoading = uiState.isLoadingCategories,
                    onCategorySelected = viewModel::onCategorySelected
                )

                // Inline Category Error handling
                if (uiState.categoriesError != null && !uiState.isLoadingCategories) {
                    Text(
                        text = uiState.categoriesError!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                    TextButton(onClick = {viewModel.retryLoadCategories()}) {
                        Text("Retry")
                    }
                }
            }
        }

        // --- SORT SECTION ---
        item(span = { GridItemSpan(maxLineSpan) }) {
            Column {
                Text(
                    text = "Sort By",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                )
                SortSection(
                    selectedSort = uiState.selectedSort,
                    onSortSelected = viewModel::onSortSelected
                )
            }
        }

        // --- PRODUCTS HEADER ---
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (uiState.selectedCategory == null) "All Products" 
                           else uiState.selectedCategory!!.name,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.weight(1f)
                )
                
                // Background Refresh Indicator (Linear)
                // We show this when we ALREADY have items but are refreshing in the background
                if (loadState.refresh is LoadState.Loading && products.itemCount > 0) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        // --- PRODUCT GRID STATES ---

        // 1. Initial Loading (No data yet)
        if (loadState.refresh is LoadState.Loading && products.itemCount == 0) {
            items(6) { ShimmerProductCard() }
        }

//         2. Initial Load Error (No data yet)
        if (loadState.refresh is LoadState.Error && products.itemCount == 0) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                ErrorMessage(
                    message = "Failed to load products",
                    onRetry = { products.retry() }
                )
            }
        }

        // 3. The Actual List
        items(
            count = products.itemCount,
            key = { index -> products.peek(index)?.id ?: index }
        ) { index ->
            products[index]?.let { product ->
                ProductCard(
                    product = product,
                    onProductClick = { onProductClicked(it.id) },
                    onWishlistClick = { viewModel.onWishlistToggle(it) },
                    isWishlisted = product.id in uiState.wishlistedIds
                )
            }
        }

        // 4. Empty State (Load finished but no results)
        if (loadState.refresh is LoadState.NotLoading && 
            loadState.append.endOfPaginationReached && 
            products.itemCount == 0) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No products found for this selection.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // 5. Append Loading (Infinite Scroll Spinner)
        if (loadState.append is LoadState.Loading) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // 6. Append Error (Retry bar at bottom)
        if (loadState.append is LoadState.Error) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                ErrorMessage(
                    message = "Couldn't load more items",
                    onRetry = { products.retry() }
                )
            }
        }

    }

}



@Composable
fun HomeTopBar(
    userName: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Hello",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = userName,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}


@Composable
fun ErrorMessage(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onRetry) {
            Text("Retry")
        }
    }
}