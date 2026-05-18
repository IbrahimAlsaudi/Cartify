package com.example.cartify.app

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cartify.app.navigation.AppBottomBar
import com.example.cartify.app.navigation.Graph
import com.example.cartify.app.navigation.RootNavHost
import com.example.cartify.app.navigation.tabs

import android.util.Log
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController

import android.annotation.SuppressLint

@Composable
fun CartifyApp() {
    val rootNavController = rememberNavController()

    // Debug Logging for Backstack
    @SuppressLint("RestrictedApi")
    LaunchedEffect(rootNavController) {
        rootNavController.currentBackStackEntryFlow.collect { _ ->
            val backstack = rootNavController.currentBackStack.value
            val routes = backstack.map { it.destination.route?.substringAfterLast('.') ?: "unknown" }
            Log.d("NavigationDebug", "Backstack: ${routes.joinToString(" -> ")}")
        }
    }

    val navBackStackEntry by rootNavController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination


    val shouldShowBottomBar = currentDestination?.let { dest ->

        val isInMainGraph = dest.hierarchy.any { it.hasRoute<Graph.Main>() }
//        isInMainGraph
        val isAtTabStart = tabs.any { tab ->
            dest.hasRoute(tab.startScreen::class)

        }
        isInMainGraph && isAtTabStart
    } ?: false

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar) {
                AppBottomBar(
                    navController = rootNavController,

                )
            }
        }
    ) { innerPadding ->
        RootNavHost(
            rootNavController = rootNavController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}