package com.example.cartify.feature.profile.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.cartify.core.domain.model.User
import com.example.cartify.feature.auth.presentation.AuthViewModel
import com.example.cartify.ui.theme.HankenGrotesk

@Composable
fun ProfileScreen(
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit = {}
) {
    val authState by authViewModel.authState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {

        val user = authState
        if (user != null && !user.isAnonymous) {
            AuthenticatedProfile(
                user = user,
                onLogout = authViewModel::logout,
                onDeleteAccount = authViewModel::deleteAccount
            )
        } else {
            GuestProfile(
                onNavigateToLogin = onNavigateToLogin
            )
        }
    }
}

@Composable
fun AuthenticatedProfile(
    user: User,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme
    
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "PROFILE",
            style = MaterialTheme.typography.labelLarge.copy(
                fontFamily = HankenGrotesk,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = colorScheme.primary
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = user.email,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = HankenGrotesk,
                color = colorScheme.onSurface.copy(alpha = 0.6f)
            )
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Logout Button
        Button(
            onClick = onLogout,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorScheme.primary
            ),
            shape = RectangleShape
        ) {
            Text(
                text = "LOGOUT",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontFamily = HankenGrotesk,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Delete Account Button
        OutlinedButton(
            onClick = onDeleteAccount,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = colorScheme.error
            ),
            shape = RectangleShape,
            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                brush = androidx.compose.ui.graphics.SolidColor(colorScheme.error.copy(alpha = 0.2f))
            )
        ) {
            Text(
                text = "DELETE ACCOUNT",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontFamily = HankenGrotesk,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            )
        }
    }
}

@Composable
fun GuestProfile(
    onNavigateToLogin: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "GUEST MODE",
            style = MaterialTheme.typography.labelLarge.copy(
                fontFamily = HankenGrotesk,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = colorScheme.onSurface.copy(alpha = 0.4f)
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Log in to manage your account and view order history.",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = HankenGrotesk,
                color = colorScheme.onSurface.copy(alpha = 0.6f)
            ),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onNavigateToLogin,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RectangleShape
        ) {
            Text(
                text = "SIGN IN",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontFamily = HankenGrotesk,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.sp
                )
            )
        }
    }
}
