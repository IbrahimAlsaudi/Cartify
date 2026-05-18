package com.example.cartify.feature.auth.presentation.login


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToMain: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Button(
            onClick = onNavigateToRegister
        ) {
            Text("Register")
        }
        Button(
            onClick = onNavigateToMain
        ) {
            Text("Main")
        }
        Button(
            onClick = onNavigateToForgotPassword
        ) {
            Text("Forgot Password")
        }
    }
}