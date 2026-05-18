package com.example.cartify.app.navigation

import androidx.compose.ui.graphics.vector.ImageVector
import com.example.cartify.R


data class TabItem(val name: String, val graph: Graph, val startScreen: Screen, val unselectedIcon: Int, val selectedIcon: Int)

val tabs = listOf(
    TabItem("Home", Graph.Home, Screen.HomeScreen,R.drawable.outline_home_24, R.drawable.baseline_home_filled_24),
    TabItem("Search", Graph.Search,Screen.SearchScreen, R.drawable.outline_search_24, R.drawable.baseline_search_24),
    TabItem("Cart", Graph.Cart, Screen.CartScreen,R.drawable.outline_shopping_bag_24, R.drawable.baseline_shopping_bag_24),
    TabItem("Wishlist", Graph.Wishlist,Screen.WishlistScreen, R.drawable.baseline_favorite_border_24,R.drawable.baseline_favorite_24),
    TabItem("Profile", Graph.Profile,Screen.ProfileScreen, R.drawable.baseline_person_outline_24,R.drawable.baseline_person_24)
)