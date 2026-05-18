package com.example.cartify.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.example.cartify.feature.auth.presentation.forgot.ForgotPasswordScreen
import com.example.cartify.feature.auth.presentation.login.LoginScreen
import com.example.cartify.feature.auth.presentation.register.RegisterScreen
import com.example.cartify.feature.auth.presentation.splash.SplashScreen
import com.example.cartify.feature.cart.presentation.CartScreen
import com.example.cartify.feature.cart.presentation.CartViewModel
import com.example.cartify.feature.home.presentation.home.HomeScreen
import com.example.cartify.feature.home.presentation.home.HomeViewModel
import com.example.cartify.feature.profile.presentation.OrderDetailViewModel
import com.example.cartify.feature.profile.presentation.OrderDetailsScreen
import com.example.cartify.feature.profile.presentation.OrderHistoryScreen
import com.example.cartify.feature.profile.presentation.ProfileScreen
import com.example.cartify.feature.search.presentation.SearchScreen
import com.example.cartify.feature.search.presentation.SearchViewModel
import com.example.cartify.feature.shared.ProductDetailScreen
import com.example.cartify.feature.shared.ProductDetailViewModel
import com.example.cartify.feature.wishlist.presentation.WishlistScreen
import com.example.cartify.feature.wishlist.presentation.WishlistViewModel

@Composable
fun RootNavHost(
    modifier: Modifier = Modifier,
    rootNavController: NavHostController
) {

    NavHost(
        navController = rootNavController,
        startDestination = Graph.Auth,
        modifier = modifier
    ) {
        // --AUTHENTICATION GRAPH--
        navigation<Graph.Auth>(startDestination = Screen.SplashScreen) {
            composable<Screen.SplashScreen> {
                SplashScreen(
                    onNavigateToMain = {
                        rootNavController.navigate(Graph.Main) {
                            popUpTo(Graph.Auth) { inclusive = true }
                        }
                    },
                    onNavigateToLogin = {
                        rootNavController.navigate(Screen.LoginScreen) {
                            // NOT ALLOWING THE USER TO MOVE BACK TO SPLASH SCREEN
                            popUpTo(Screen.SplashScreen) {inclusive = true}
                        }
                    }
                )
            }
            composable<Screen.LoginScreen> {
                LoginScreen(
                    onNavigateToMain = {
                        rootNavController.navigate(Graph.Main) {
                            // NOT ALLOWING THE USER TO COME BACK AFTER THE LOGIN
                            popUpTo(Graph.Auth) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { rootNavController.navigate(Screen.RegisterScreen) },
                    onNavigateToForgotPassword = { rootNavController.navigate(Screen.ForgetPasswordScreen) }
                )
            }
            composable<Screen.RegisterScreen> { RegisterScreen(onRegisterClicked = {rootNavController.navigateUp()}) }
            composable<Screen.ForgetPasswordScreen> {
                ForgotPasswordScreen(
                    onNavigateBack = {
                        rootNavController.navigateUp()
                    }
                )
            }
        }
        // --MAIN GRAPH
        navigation<Graph.Main>(startDestination = Graph.Home) {
            // -- SUB-GRAPH: HOME --
            navigation<Graph.Home>(startDestination = Screen.HomeScreen) {
                composable<Screen.HomeScreen> {
                    val viewModel: HomeViewModel = hiltViewModel()
                    HomeScreen(
                        viewModel = viewModel,
                        onProductClicked = { id ->
                            rootNavController.navigate(Screen.ProductDetailScreen(id))
                        },
                        onSearchClicked = {
                            rootNavController.navigate(Graph.Search) {
                                popUpTo(Graph.Home) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
                composable<Screen.ProductDetailScreen> {
                    // ViewModel pulls its own 'id' from SavedStateHandle
                    val viewModel: ProductDetailViewModel = hiltViewModel()
                    ProductDetailScreen(
                        viewModel = viewModel,
                        onBackClick = { rootNavController.navigateUp() }
                    )
                }
            }

            // --Sub-Graph Search
            navigation<Graph.Search>(startDestination = Screen.SearchScreen) {
                composable<Screen.SearchScreen> {
                    val viewModel: SearchViewModel = hiltViewModel()
                    SearchScreen(viewModel = viewModel)
                }
                composable<Screen.ProductDetailScreen> {
                    val viewModel: ProductDetailViewModel = hiltViewModel()
                    ProductDetailScreen(
                        viewModel = viewModel,
                        onBackClick = {rootNavController.navigateUp()}
                    )
                }
            }

            // -- Sub-Graph Cart
            navigation<Graph.Cart>(startDestination = Screen.CartScreen) {
                composable<Screen.CartScreen> {
                val viewModel: CartViewModel = hiltViewModel()
                    CartScreen(viewModel = viewModel, navigateToDetails = { id ->
                        rootNavController.navigate(Screen.ProductDetailScreen(id))
                    })
                }

                composable<Screen.ProductDetailScreen> {
                    val viewModel: ProductDetailViewModel = hiltViewModel()
                    ProductDetailScreen(viewModel, onBackClick = {rootNavController.navigateUp()})
                }
            }

            // --Sub-Graph Wishlist
            navigation<Graph.Wishlist>(startDestination = Screen.WishlistScreen) {
                composable<Screen.WishlistScreen> {
                    val viewModel: WishlistViewModel = hiltViewModel()
                    WishlistScreen(viewModel = viewModel,
                        onProductClick = { id ->
                            rootNavController.navigate(Screen.ProductDetailScreen(id))
                        },)
                }
                composable<Screen.ProductDetailScreen> {
                    val viewModel: ProductDetailViewModel = hiltViewModel()
                    ProductDetailScreen(
                        viewModel = viewModel,
                        onBackClick = {rootNavController.navigateUp()}
                    )
                }
            }
            // -- Sub-Graph Profile
            navigation<Graph.Profile>(startDestination = Screen.ProfileScreen) {
                composable<Screen.ProfileScreen> {
                    ProfileScreen()
                }
                composable<Screen.OrderDetailScreen> {
                    val viewModel: OrderDetailViewModel = hiltViewModel()
                    OrderDetailsScreen(viewModel = viewModel)
                }
                composable<Screen.OrderHistoryScreen> {

                    OrderHistoryScreen()
                }

            }
        }
    }
}