package com.example.cartify.feature.profile.presentation


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.SolidColor
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
    onNavigateToLogin: () -> Unit = {},
    onNavigateToOrders: () -> Unit = {}
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
                onDeleteAccount = authViewModel::deleteAccount,
                onNavigateToOrders = onNavigateToOrders
            )
        } else {
            GuestProfile(onNavigateToLogin = onNavigateToLogin)
        }
    }
}

@Composable
fun AuthenticatedProfile(
    user: User,
    onLogout: () -> Unit,
    onDeleteAccount: () -> Unit,
    onNavigateToOrders: () -> Unit
) {
    val colorScheme = MaterialTheme.colorScheme

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {

        // ── Avatar initials circle ────────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = colorScheme.primary.copy(alpha = 0.1f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = user.name
                    .split(" ")
                    .mapNotNull { it.firstOrNull()?.uppercaseChar() }
                    .take(2)
                    .joinToString(""),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = HankenGrotesk,
                    fontWeight = FontWeight.Bold,
                    color = colorScheme.primary
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ── Name ─────────────────────────────────────────────────────────────
        Text(
            text = user.name,
            style = MaterialTheme.typography.titleLarge.copy(
                fontFamily = HankenGrotesk,
                fontWeight = FontWeight.Bold,
                color = colorScheme.onSurface
            )
        )

        Spacer(modifier = Modifier.height(4.dp))

        // ── Email ─────────────────────────────────────────────────────────────
        Text(
            text = user.email,
            style = MaterialTheme.typography.bodyMedium.copy(
                fontFamily = HankenGrotesk,
                color = colorScheme.onSurface.copy(alpha = 0.6f)
            )
        )

        Spacer(modifier = Modifier.height(48.dp))

        // ── Action buttons ────────────────────────────────────────────────────

        // My Orders
        ProfileActionButton(
            label = "MY ORDERS",
            onClick = onNavigateToOrders,
            containerColor = colorScheme.secondaryContainer,
            contentColor = colorScheme.onSecondaryContainer
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Logout
        ProfileActionButton(
            label = "LOGOUT",
            onClick = onLogout,
            containerColor = colorScheme.primary,
            contentColor = colorScheme.onPrimary
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Delete Account
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
                brush = SolidColor(colorScheme.error.copy(alpha = 0.2f))
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
fun ProfileActionButton(
    label: String,
    onClick: () -> Unit,
    containerColor: Color,
    contentColor: Color
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        shape = RectangleShape
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge.copy(
                fontFamily = HankenGrotesk,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
        )
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

        // ── Guest avatar ──────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(
                    color = colorScheme.onSurface.copy(alpha = 0.06f),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {

        }

        Spacer(modifier = Modifier.height(16.dp))

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