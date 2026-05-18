package com.example.cartify.app.navigation

import kotlinx.serialization.Serializable

// --- 1. THE FLOWS (The Containers) ---
// These are the "Lanes" or "Folders"
@Serializable
sealed interface Graph {

    @Serializable data object Auth : Graph
    @Serializable data object Main : Graph

    // The Tabs
    @Serializable data object Home : Graph
    @Serializable data object Search : Graph
    @Serializable data object Cart : Graph
    @Serializable data object Wishlist : Graph
    @Serializable data object Profile : Graph
}

// --- 2. THE DESTINATIONS (The Actual UI) ---
// These are the "Files" inside those folders
@Serializable
sealed interface Screen {
    @Serializable data object SplashScreen: Screen
    // Auth
    @Serializable data object LoginScreen : Screen
    @Serializable data object RegisterScreen : Screen
    @Serializable data object ForgetPasswordScreen : Screen

    // Main
//    @Serializable object MainScreen: Screen
    @Serializable data object HomeScreen : Screen
    @Serializable data object SearchScreen : Screen
    @Serializable data object CartScreen : Screen
    @Serializable data object WishlistScreen : Screen
    @Serializable data object ProfileScreen : Screen
    @Serializable data object OrderHistoryScreen: Screen
    @Serializable data class OrderDetailScreen(val id: String): Screen
    @Serializable data class ProductDetailScreen(val id: Int) : Screen
}