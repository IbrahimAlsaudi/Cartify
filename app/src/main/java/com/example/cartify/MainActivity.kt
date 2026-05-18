package com.example.cartify

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.cartify.app.CartifyApp
import com.example.cartify.app.navigation.RootNavHost
import com.example.cartify.ui.theme.CartifyTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CartifyTheme {
                CartifyApp()
            }
        }
    }
}

