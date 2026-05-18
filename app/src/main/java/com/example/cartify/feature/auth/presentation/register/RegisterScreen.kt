package com.example.cartify.feature.auth.presentation.register



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun RegisterScreen(onRegisterClicked: () -> Unit) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center,) {
        Button(
            onClick = onRegisterClicked
        ) {
            Text("Register")
        }
    }
}