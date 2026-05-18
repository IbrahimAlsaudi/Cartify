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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.cartify.R
import com.example.cartify.core.util.Constants

@Composable
fun HomeScreen(
    onProductClicked: (Int) -> Unit,
    onSearchClicked: () -> Unit,
    viewModel: HomeViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val products = viewModel.products.collectAsLazyPagingItems()

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier = Modifier
            .fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp, start = 16.dp, end = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {


        item(span = { GridItemSpan(maxLineSpan) }) {
            HomeTopBar(
                onCartClick = { },
                onNotificationClick = { }
            )
        }

        // Search bar — full width
        item(span = { GridItemSpan(maxLineSpan) }) {
            SearchBarButton(
                modifier = Modifier
                    .fillMaxWidth(),
                onClick = onSearchClicked
            )
        }

        // Banner — full width
        item(span = { GridItemSpan(maxLineSpan) }) {
            BannerSection(
                banners = Constants.BANNERS,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Categories header — full width
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = "Categories",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Categories row — full width
        item(span = { GridItemSpan(maxLineSpan) }) {
            CategorySection(
                categories = uiState.categories,
                selectedCategory = uiState.selectedCategory,
                isLoading = uiState.isLoadingCategories,
                onCategorySelected = viewModel::onCategorySelected
            )
        }

        // Products header — full width
        item(span = { GridItemSpan(maxLineSpan) }) {
            Text(
                text = if (uiState.selectedCategory == null) "All Products"
                else uiState.selectedCategory!!.name,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(top = 4.dp)
            )
        }


        // Products — shimmer or grid
        val refreshState = products.loadState.refresh
//
//        if (refreshState is LoadState.Loading && products.itemCount > 0) {
//            item(span = { GridItemSpan(maxLineSpan) }) {
//                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
//            }
//        }

        // 1. Initial Loading State (No data yet)
        if (refreshState is LoadState.Loading ) {
            items(
                count = 6,
                span = { GridItemSpan(1) }
            ) {
                ShimmerProductCard()
            }
        }

        // 2. Error State (No data yet)
        if (refreshState is LoadState.Error && products.itemCount == 0) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                ErrorMessage(
                    message = "Failed to load products",
                    onRetry = { products.retry() }
                )
            }
        }

        // 3. Product List (Show if we have items, even if refreshing)
        items(
            count = products.itemCount,
            key = { index -> products.peek(index)?.id ?: index },
            span = { GridItemSpan(1) }
        ) { index ->
            val product = products[index]
            product?.let { product ->
                ProductCard(
                    product = product,
                    onProductClick = { onProductClicked(it.id) },
                    onWishlistClick = { viewModel.onWishlistToggle(it) },
                    isWishlisted = product.id in uiState.wishlistedIds
                )
            }

        }

        // 4. Append loading — full width
        if (products.loadState.append is LoadState.Loading) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text("Loading ...") /*Only for testing the loading behavior*/
                }
            }
        }

        // 5. Append Error — Show a retry button at the bottom
        if (products.loadState.append is LoadState.Error) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Couldn't load more products")
                    Button(onClick = { products.retry() }) {
                        Text("Retry")
                    }
                }
            }
        }


    }
}


@Composable
fun HomeTopBar(
    onCartClick: () -> Unit,
    onNotificationClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "Good morning 👋",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Ibrahim",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Row {
            IconButton(onClick = onNotificationClick) {
                Icon(
//                    imageVector = Icons.Outlined.Notifications,
                    painter = painterResource(R.drawable.outline_notifications_24),
                    contentDescription = "Notifications"
                )
            }
            IconButton(onClick = onCartClick) {
                Icon(
//                    imageVector = Icons.Outlined.ShoppingCart,
                    painter = painterResource(R.drawable.outline_shopping_bag_24),
                    contentDescription = "Cart"
                )
            }
        }
    }
}

@Composable
fun SearchBarButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
//                imageVector = Icons.Outlined.Search,
                painter = painterResource(R.drawable.outline_search_24),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Search products...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
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