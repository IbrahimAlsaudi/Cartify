package com.example.cartify.app.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.cartify.feature.auth.presentation.AuthViewModel
import com.example.cartify.feature.auth.presentation.forgot.ForgotPasswordScreen
import com.example.cartify.feature.auth.presentation.login.LoginScreen
import com.example.cartify.feature.auth.presentation.login.LoginViewModel
import com.example.cartify.feature.auth.presentation.register.RegisterScreen
import com.example.cartify.feature.auth.presentation.register.RegisterViewModel
import com.example.cartify.feature.auth.presentation.splash.SplashScreen
import com.example.cartify.feature.auth.presentation.splash.SplashViewModel
import com.example.cartify.feature.cart.presentation.CartScreen
import com.example.cartify.feature.cart.presentation.CartViewModel
import com.example.cartify.feature.home.presentation.home.HomeScreen
import com.example.cartify.feature.home.presentation.home.HomeViewModel
import com.example.cartify.feature.profile.presentation.OrderDetailViewModel
import com.example.cartify.feature.profile.presentation.OrderDetailsScreen
import com.example.cartify.feature.profile.presentation.OrderHistoryScreen
import com.example.cartify.feature.profile.presentation.OrderHistoryViewModel
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
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState == null) {
            // Check if the user is currently inside the Main graph
            val isInMainGraph = rootNavController.currentBackStackEntry?.destination?.hierarchy?.any { 
                it.hasRoute<Graph.Main>()
            } == true

            if (isInMainGraph) {
                rootNavController.navigate(Graph.Auth) {
                    popUpTo(Graph.Main) { inclusive = true }
                }
            }
        }
    }

    NavHost(
        navController = rootNavController,
        startDestination = Graph.Auth,
        modifier = modifier
    ) {
        // --AUTHENTICATION GRAPH--
        navigation<Graph.Auth>(startDestination = Screen.SplashScreen) {
            composable<Screen.SplashScreen> {
                val viewModel: SplashViewModel = hiltViewModel()
                SplashScreen(
                    viewModel = viewModel,
                    onNavigateToMain = {
                        rootNavController.navigate(Graph.Main) {
                            popUpTo(Graph.Auth) { inclusive = true }
                        }
                    },
                    onNavigateToSignin = {
                        rootNavController.navigate(Screen.LoginScreen) {
                            popUpTo(Screen.SplashScreen) { inclusive = true}
                        }
                    },


                )
            }
            composable<Screen.LoginScreen> {
                val viewModel: LoginViewModel = hiltViewModel()
                LoginScreen(
                    viewModel = viewModel,
                    onNavigateToMain = {
                        rootNavController.navigate(Graph.Main) {
                            // NOT ALLOWING THE USER TO COME BACK AFTER THE LOGIN
                            popUpTo(Graph.Auth) { inclusive = true }
                        }
                    },
                    onNavigateToRegister = { rootNavController.navigate(Screen.RegisterScreen) },
                    onNavigateToForgotPassword = { rootNavController.navigate(Screen.ForgetPasswordScreen) },
                     joinAsGuest = {
                         rootNavController.navigate(Graph.Main) {
                             // NOT ALLOWING THE USER TO COME BACK AFTER THE LOGIN
                             popUpTo(Graph.Auth) { inclusive = true }
                         }
                     },
                    )
            }
            composable<Screen.RegisterScreen> {
                val viewModel: RegisterViewModel = hiltViewModel()
                RegisterScreen(
                    viewModel = viewModel,
                    onNavigateBack = { rootNavController.popBackStack() },
                    onNavigateToMain = {
                        rootNavController.navigate(Graph.Main) {
                            popUpTo(Graph.Auth) { inclusive = true }
                        }
                    }
                ) 
            }
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
                    SearchScreen(viewModel = viewModel,
                        onProductClicked = {rootNavController.navigate(Screen.ProductDetailScreen(it))})
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
                    CartScreen(
                        viewModel = viewModel, navigateToDetails = { id ->
                            rootNavController.navigate(Screen.ProductDetailScreen(id))
                        },
                        navigateToRegister = {
                            rootNavController.navigate(Screen.RegisterScreen)
                        }
                    )
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
                    ProfileScreen(
                        onNavigateToLogin = {
                            rootNavController.navigate(Screen.LoginScreen)
                           /* {
                                popUpTo(Graph.Main) { inclusive = true }
                            }*/
                        },
                        onNavigateToOrders = {
                            rootNavController.navigate(Screen.OrderHistoryScreen)
                        }
                    )
                }
                composable<Screen.OrderDetailScreen> {
                    val viewModel: OrderDetailViewModel = hiltViewModel()
                    OrderDetailsScreen(
                        viewModel = viewModel,
                        onNavigateBack = { rootNavController.popBackStack() }
                    )
                }
                composable<Screen.OrderHistoryScreen> {
                    val viewModel: OrderHistoryViewModel = hiltViewModel()
                    OrderHistoryScreen(
                        viewModel = viewModel,
                        onNavigateBack = { rootNavController.popBackStack() },
                        onNavigateToOrderDetail = { id ->
                            rootNavController.navigate(Screen.OrderDetailScreen(id))
                        }
                    )
                }

            }
        }
    }
}