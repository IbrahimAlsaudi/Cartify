package com.example.cartify.feature.auth.presentation.forgot

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ForgotPasswordScreen(
    modifier: Modifier = Modifier,
    onNavigateBack: () -> Unit
) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,) {
        Button(
            onClick = onNavigateBack
        ) {
            Text("Back")
        }

    }
}