package com.example.cartify.feature.auth.presentation.login

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cartify.R
import com.example.cartify.ui.theme.PlayfairDisplay
import com.example.cartify.ui.theme.HankenGrotesk

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    onNavigateToRegister: () -> Unit,
    onNavigateToMain: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    joinAsGuest: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val colorScheme = MaterialTheme.colorScheme

    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) onNavigateToMain()
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            colorScheme.surfaceVariant, // Using surfaceVariant for the lighter part of gradient
                            colorScheme.background
                        ),
                        center = Offset(500f, -200f),
                        radius = 1500f
                    )
                )
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CARTIFY",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = PlayfairDisplay,
                            letterSpacing = 4.sp,
                            fontWeight = FontWeight.Light,
                            color = colorScheme.primary
                        )
                    )
//                    IconButton(onClick = { /* Handle Close */ }) {
//                        Icon(
//                            painter = painterResource(id = R.drawable.outline_close_small_24),
//                            contentDescription = "Close",
//                            tint = colorScheme.primary
//                        )
//                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Main Content
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome Back",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = PlayfairDisplay,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Normal,
                            color = colorScheme.onSurface
                        ),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Enter your details to access your curated collection.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontFamily = HankenGrotesk,
                            color = colorScheme.onSurface.copy(alpha = 0.6f)
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Email Input
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "EMAIL ADDRESS",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontFamily = HankenGrotesk,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                color = colorScheme.primary.copy(alpha = 0.8f)
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = uiState.email,
                            onValueChange = { viewModel.onEmailChanged(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colorScheme.surfaceVariant),
                            placeholder = {
                                Text(
                                    "name@example.com",
                                    color = colorScheme.onSurface.copy(alpha = 0.3f)
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colorScheme.primary,
                                unfocusedBorderColor = colorScheme.primary.copy(alpha = 0.2f),
                                cursorColor = colorScheme.primary,
                                focusedTextColor = colorScheme.onSurface,
                                unfocusedTextColor = colorScheme.onSurface
                            ),
                            shape = RectangleShape,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Email,
                                imeAction = ImeAction.Next
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Password Input
                    Column(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "PASSWORD",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontFamily = HankenGrotesk,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.sp,
                                    color = colorScheme.primary.copy(alpha = 0.8f)
                                )
                            )
                            Text(
                                text = "Forgot Password?",
                                modifier = Modifier.clickable { onNavigateToForgotPassword() },
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = HankenGrotesk,
                                    color = colorScheme.primary
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = uiState.password,
                            onValueChange = { viewModel.onPasswordChanged(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(colorScheme.surfaceVariant),
                            placeholder = {
                                Text(
                                    "••••••••",
                                    color = colorScheme.onSurface.copy(alpha = 0.3f)
                                )
                            },
                            visualTransformation = if (uiState.isPasswordShown) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                TextButton(onClick = { viewModel.onIsPasswordChanged(uiState.isPasswordShown) }) {
                                    Text(
                                        text = if (uiState.isPasswordShown) "HIDE" else "SHOW",
                                        color = colorScheme.onSurface.copy(alpha = 0.5f),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontFamily = HankenGrotesk,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                }
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = colorScheme.primary,
                                unfocusedBorderColor = colorScheme.primary.copy(alpha = 0.2f),
                                cursorColor = colorScheme.primary,
                                focusedTextColor = colorScheme.onSurface,
                                unfocusedTextColor = colorScheme.onSurface
                            ),
                            shape = RectangleShape,
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password,
                                imeAction = ImeAction.Done
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Login Button
                    Button(
                        onClick = {
                            viewModel.loginWithEmailAndPassword(uiState.email, uiState.password)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorScheme.primary,
                            contentColor = colorScheme.onPrimary
                        ),
                        shape = RectangleShape
                    ) {
                        Text(
                            text = "LOGIN",
                            style = MaterialTheme.typography.labelLarge.copy(
                                fontFamily = HankenGrotesk,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 2.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // OR Divider
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = colorScheme.primary.copy(alpha = 0.1f)
                        )
                        Text(
                            text = "OR",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontFamily = HankenGrotesk,
                                color = colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        )
                        HorizontalDivider(
                            modifier = Modifier.weight(1f),
                            color = colorScheme.primary.copy(alpha = 0.1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Google Sign In Button
                    OutlinedButton(
                        onClick = { viewModel.loginWithGoogle(context as Activity)},
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = colorScheme.onSurface
                        ),
                        shape = RectangleShape,
                        border = BorderStroke(1.dp, colorScheme.onSurface.copy(alpha = 0.1f))
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "SIGN IN WITH GOOGLE",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontFamily = HankenGrotesk,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 2.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(48.dp))

                    // Bottom Links
                    Text(
                        text = "JOIN AS A GUEST",
                        modifier = Modifier
                            .clickable { viewModel.joinAsGuest() }
                            .padding(bottom = 4.dp),
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontFamily = HankenGrotesk,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = colorScheme.primary
                        )
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row {
                        Text(
                            text = "New to Cartify? ",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = HankenGrotesk,
                                color = colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        )
                        Text(
                            text = "Create Account",
                            modifier = Modifier.clickable { onNavigateToRegister() },
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = HankenGrotesk,
                                fontWeight = FontWeight.SemiBold,
                                color = colorScheme.onSurface
                            )
                        )
                    }
                }
            }
        }
    }
}
