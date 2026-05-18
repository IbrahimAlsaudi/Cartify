package com.example.cartify.app.navigation

import android.util.Log
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.SegmentedButtonDefaults.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState


@Composable
fun AppBottomBar(
    navController: NavHostController,
) {
    // 1. Observe the current backstack to know where we are
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // Debug Logging for Backstack
//    LaunchedEffect(navController) {
//        navController.currentBackStackEntryFlow.collect { entry ->
//            val backstack = navController.currentBackStack.value
//            val routes = backstack.map { it.destination.route?.substringAfterLast('.') ?: "unknown" }
//            Log.d("NavigationDebug", "Backstack: ${routes.joinToString(" -> ")}")
//        }
//    }

    NavigationBar {
        tabs.forEach { tab ->
            val isSelected = currentDestination?.hierarchy?.any {
                it.hasRoute(tab.graph::class)
            } == true

            NavigationBarItem(
                selected = isSelected,
//                label = { Text(tab.name) },
                icon = {
                    Icon(painter = if (isSelected) painterResource(tab.selectedIcon) else painterResource(tab.unselectedIcon)
                        , contentDescription = tab.name)
                       },
                onClick = {
                    if(!isSelected){
                        navController.navigate(tab.graph) {
                            popUpTo<Graph.Main> {
                                saveState = true
                            }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }else {
                        navController.popBackStack(
                            route = tab.startScreen,
                            inclusive = false
                        )
                    }

                }
            )
        }
    }
}

