package com.example.cartify.feature.auth.presentation.register

import android.app.Activity
import androidx.compose.animation.*
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.font.FontStyle
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
fun RegisterScreen(
    viewModel: RegisterViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToMain: () -> Unit,
) {



    val uiState by viewModel.uiState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme
    val context = LocalContext.current

    // Entrance Animation State
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        visible = true
    }
    val snackbarHostState = remember { SnackbarHostState() }

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
        snackbarHost = {SnackbarHost(snackbarHostState)}
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(colorScheme.background)
                .padding(innerPadding)
        ) {
            // Atmospheric Glow
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                colorScheme.primary.copy(alpha = 0.03f),
                                Color.Transparent
                            ),
                            center = Offset(500f, 1000f),
                            radius = 2000f
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Header
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 24.dp)
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.outline_close_small_24),
                            contentDescription = "Back",
                            tint = colorScheme.primary
                        )
                    }
                    Text(
                        text = "CARTIFY",
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontFamily = PlayfairDisplay,
                            letterSpacing = 4.sp,
                            fontWeight = FontWeight.Light,
                            color = colorScheme.primary
                        )
                    )
                }

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(animationSpec = tween(1000)) +
                            slideInVertically(initialOffsetY = { 40 }, animationSpec = tween(1000))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp, vertical = 48.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Headline Section
                        Text(
                            text = "Join the Collection",
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontFamily = PlayfairDisplay,
                                fontSize = 32.sp,
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.Normal,
                                color = colorScheme.onSurface
                            ),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Experience curated exclusivity and personalized elegance across every touchpoint.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontFamily = HankenGrotesk,
                                color = colorScheme.onSurface.copy(alpha = 0.6f)
                            ),
                            modifier = Modifier.width(320.dp),
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(64.dp))

                        // Registration Form
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(32.dp)
                        ) {
                            // Full Name Input
                            LuxuryUnderlinedTextField(
                                value = uiState.name,
                                onValueChange = { viewModel.onNameChanged(it) },
                                label = "FULL NAME",
                                placeholder = "ALEXANDER VOGUE",
                                colorScheme = colorScheme
                            )

                            // Email Input
                            LuxuryUnderlinedTextField(
                                value = uiState.email,
                                onValueChange = { viewModel.onEmailChanged(it) },
                                label = "EMAIL ADDRESS",
                                placeholder = "CURATOR@CARTIFY.COM",
                                keyboardType = KeyboardType.Email,
                                colorScheme = colorScheme
                            )

                            // Password Input
                            LuxuryUnderlinedTextField(
                                value = uiState.password,
                                onValueChange = { viewModel.onPasswordChanged(it) },
                                label = "PASSWORD",
                                placeholder = "••••••••",
                                keyboardType = KeyboardType.Password,
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
                                colorScheme = colorScheme
                            )
                        }

                        Spacer(modifier = Modifier.height(48.dp))

                        // Primary CTA
                        Button(
                            onClick = {
                                viewModel.signUp(
                                    uiState.name,
                                    uiState.email,
                                    uiState.password
                                )
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
                                text = "CREATE ACCOUNT",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontFamily = HankenGrotesk,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 4.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Divider
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = colorScheme.onSurface.copy(alpha = 0.1f)
                            )
                            Text(
                                text = "OR",
                                modifier = Modifier.padding(horizontal = 16.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontFamily = HankenGrotesk,
                                    color = colorScheme.onSurface.copy(alpha = 0.4f),
                                    letterSpacing = 2.sp
                                )
                            )
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = colorScheme.onSurface.copy(alpha = 0.1f)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Social CTA (Google)
                        OutlinedButton(
                            onClick = { viewModel.loginWithGoogle(context as Activity) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RectangleShape,
                            border = BorderStroke(
                                1.dp,
                                colorScheme.onSurface.copy(alpha = 0.1f)
                            )
                        ) {
                            Text(
                                text = "SIGN IN WITH GOOGLE",
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontFamily = HankenGrotesk,
                                    color = colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 2.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(48.dp))

                        // Bottom Tertiary Action (Join as Guest)
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            HorizontalDivider(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .alpha(0.1f),
                                color = colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(32.dp))
                            Row(
                                modifier = Modifier
                                    .clickable { viewModel.joinAsGuest() }
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "JOIN AS A GUEST",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontFamily = HankenGrotesk,
                                        color = colorScheme.onSurface.copy(alpha = 0.6f),
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 2.sp
                                    )
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    painter = painterResource(id = R.drawable.baseline_arrow_forward_24),
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(48.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LuxuryUnderlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    placeholder: String,
    keyboardType: KeyboardType = KeyboardType.Text,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    trailingIcon: @Composable (() -> Unit)? = null,
    colorScheme: ColorScheme
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = HankenGrotesk,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = colorScheme.onSurface.copy(alpha = 0.4f)
            )
        )
        TextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = HankenGrotesk,
                        color = colorScheme.onSurface.copy(alpha = 0.2f)
                    )
                )
            },
            visualTransformation = visualTransformation,
            trailingIcon = trailingIcon,
            keyboardOptions = KeyboardOptions(
                keyboardType = keyboardType,
                imeAction = ImeAction.Next
            ),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
                focusedIndicatorColor = colorScheme.primary,
                unfocusedIndicatorColor = colorScheme.onSurface.copy(alpha = 0.1f),
                cursorColor = colorScheme.primary,
                focusedTextColor = colorScheme.onSurface,
                unfocusedTextColor = colorScheme.onSurface
            ),
            shape = RectangleShape,
            singleLine = true
        )
    }
}
