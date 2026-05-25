package com.example.cartify.feature.wishlist.presentation

import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.example.cartify.R
import com.example.cartify.core.domain.model.WishListItem
import com.example.cartify.feature.home.presentation.home.ErrorMessage
import com.example.cartify.feature.home.presentation.components.ShimmerProductCard

@Composable
fun WishlistScreen(
    viewModel: WishlistViewModel,
    onProductClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val products = viewModel.products.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Wishlist",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        if (uiState.count == 0 && products.loadState.refresh !is LoadState.Loading) {
            item {
                Box(
                    modifier = Modifier
                        .fillParentMaxHeight(0.7f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No Wishlist items yet :(",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else if (uiState.count > 0) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${uiState.count} items saved for later",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "CLEAR ALL",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable {
                            viewModel.deleteAllWishlist()
                        }
                    )
                }
            }
        }

        val refreshState = products.loadState.refresh

        if (refreshState is LoadState.Loading) {
            items(count = 6) {
                ShimmerProductCard()
            }
        }

        if (refreshState is LoadState.Error && products.itemCount == 0) {
            item {
                ErrorMessage(
                    message = "Failed to load products",
                    onRetry = { products.retry() }
                )
            }
        }

        items(
            count = products.itemCount,
            key = { index -> products.peek(index)?.productId ?: index }
        ) { index ->
            val product = products[index]
            // We check for null here because Paging 3 items are nullable to support placeholders
            product?.let { item ->
                WishlistCard(
                    product = item,
                    onProductClick = { onProductClick(it.productId) },
                    onWishlistClick = { viewModel.deleteWishlistItem(item.productId) },
                    onAddToCart = {viewModel.addToCart(it)}
                )
            }
        }
    }
}

@Composable
fun WishlistCard(
    product: WishListItem,
    onProductClick: (WishListItem) -> Unit,
    onWishlistClick: () -> Unit,
    onAddToCart: (WishListItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onProductClick(product) },
//        shape = RoundedCornerShape(12.dp),
        shape = RectangleShape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            Box {
                // Product image
                AsyncImage(
                    model = product.thumbnail,
                    contentDescription = product.title,
                    contentScale = ContentScale.Crop,

                    modifier = Modifier
                        .fillMaxWidth()
                        .height(350.dp)
                        .background(
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.3f)
                        )
                )
                // delete button
                IconButton(
                    modifier = Modifier
                        .padding(top = 16.dp, end = 16.dp)
                        .align(Alignment.TopEnd)
                        .height(32.dp)
                        .width(24.dp)
                    ,
                    colors = IconButtonDefaults.iconButtonColors(
                        containerColor = Color.Black.copy(alpha = 0.3f),
                        contentColor = Color.White.copy(alpha = 0.7f)
                    ),

                    onClick = onWishlistClick
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_close_small_24),
                        contentDescription = "Delete item from wishlist"
                    )
                }
            }

            // Product info
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                Text(
                    text = product.title,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 8.dp)
                )
                
                Spacer(modifier = Modifier.height(4.dp))

                // Price
                Text(
                    text = "$${product.price}",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 8.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    shape = RectangleShape,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    onClick = { onAddToCart(product) },
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.outline_shopping_cart_24),
                            contentDescription = null
                        )
                        Spacer(Modifier.width(8.dp))
                        Text("ADD TO CART")
                    }
                }
            }
        }
    }
}
