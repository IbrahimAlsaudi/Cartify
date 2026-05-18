package com.example.cartify.feature.auth.presentation.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun SplashScreen(
    onNavigateToMain: () -> Unit,
    onNavigateToLogin:() -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,) {
        Button(
            onClick = onNavigateToLogin
        ) {
            Text("Login")
        }

        Button(
            onClick = onNavigateToMain
        ) {
            Text("Main")
        }
    }
}