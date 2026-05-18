package com.example.cartify.feature.search.presentation

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import com.example.cartify.R
import com.example.cartify.feature.home.presentation.home.ProductCard

@Composable
fun SearchScreen(
    viewModel: SearchViewModel
) {
    val products = viewModel.products.collectAsLazyPagingItems()
    val query by viewModel.searchQuery.collectAsState()
    Scaffold() { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            item {
                MyTextField(
                    value = query,
                    onValueChanged = { viewModel.updateSearchQuery(it) },
                    onSearchClicked = { viewModel.searchProducts(it) },
                    onDeleteClicked = {}
                )
            }

            items(
                count = products.itemCount,
                key = { index -> products.peek(index)?.id ?: index },
            ) { index ->
                val product = products[index]
                product?.let { product ->
                    ProductCard(
                        product = product,
                        onProductClick = {  },
                        onWishlistClick = {},
                        isWishlisted = false
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
    TextField(
        value = value,
        onValueChange = onValueChanged,
        modifier = modifier.fillMaxWidth(),
        leadingIcon = {
            IconButton(onClick = { onSearchClicked(value) }) {
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
                onSearchClicked(value)
            }
        )
    )
}