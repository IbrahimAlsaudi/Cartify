package com.example.cartify.feature.search.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.cartify.R
import com.example.cartify.feature.home.presentation.components.ProductCard
import kotlin.text.append

@Composable
fun SearchScreen(
    viewModel: SearchViewModel,
    onProductClicked: (Int) -> Unit,
) {
    val products = viewModel.products.collectAsLazyPagingItems()
    val uiState by viewModel.uiState.collectAsState()
    Scaffold(
        topBar = {
            MyTextField(
                value = uiState.query,
                onValueChanged = { viewModel.updateSearchQuery(it) },
                onSearchClicked = { viewModel.searchProducts(it) },
                onDeleteClicked = {viewModel.clearSearchQuery()},
                modifier = Modifier.padding(start = 16.dp, top = 24.dp, end = 16.dp)
            )
        }
    ) { innerPadding ->
        val loadState = products.loadState

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            modifier = Modifier.fillMaxSize().padding(innerPadding),
            contentPadding = PaddingValues(bottom = 16.dp, start = 16.dp, end = 16.dp, top = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if(uiState.query.isEmpty() && products.itemCount == 0) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        contentAlignment = Alignment.CenterStart
                    ) {
                            Text(
                                text = "Start searching some amazing product from our collection",
                                style = MaterialTheme.typography.titleLarge
                            )

                    }
                }
            }

            if (loadState.refresh is LoadState.NotLoading &&
                loadState.append.endOfPaginationReached &&
                products.itemCount == 0) {

                item(span = { GridItemSpan(maxLineSpan) }) {
                    Box(
                        modifier = Modifier.fillMaxWidth().padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "No items found :(",
                                style = MaterialTheme.typography.titleLarge
                            )
                            Text(
                                text = "Try a different search",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

                items(
                    count = products.itemCount,
                    key = { index -> products.peek(index)?.id ?: index },
                ) { index ->
                    val product = products[index]
                    product?.let { product ->
                        ProductCard(
                            product = product,
                            onProductClick = { onProductClicked(it.id) },
                            onWishlistClick = {viewModel.onWishlistToggle(it)},
                            isWishlisted = product.id in uiState.wishlistIds
                        )
                    }

                }



            }

    }
}

@Composable
fun MyTextField(
    value: String,
    onValueChanged: (String) -> Unit,
    onSearchClicked: (String) -> Unit,
    onDeleteClicked: () ->Unit,
    modifier: Modifier = Modifier
) {
    val focusManger = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
    TextField(
        value = value,
        onValueChange = onValueChanged,
        modifier = modifier.fillMaxWidth(),
        leadingIcon = {
            IconButton(onClick = {
                keyboardController?.hide()
                focusManger.clearFocus()
                onSearchClicked(value)
            }) {
                Icon(
                    painter = painterResource(R.drawable.outline_search_24),
                    contentDescription = "Search"
                )
            }
        },
        trailingIcon = {
            if(value.isNotEmpty()) {
                IconButton(onClick = onDeleteClicked) {
                    Icon(
                        painter = painterResource(R.drawable.outline_close_small_24),
                        contentDescription = "Clear Search"
                    )
                }
            }
        },
        placeholder = {
            Text("Search")
        },
        singleLine = true,
        shape = RoundedCornerShape(28.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(
            onSearch = {
                keyboardController?.hide()
                focusManger.clearFocus()
                onSearchClicked(value)
            }
        )
    )
}