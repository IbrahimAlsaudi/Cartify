package com.example.cartify.feature.auth.presentation.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.example.cartify.R
import com.example.cartify.ui.theme.HankenGrotesk
import com.example.cartify.ui.theme.PlayfairDisplay
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onNavigateToMain: () -> Unit,
    onNavigateToSignin: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val colorScheme = MaterialTheme.colorScheme

    // Animations
    var startReveal by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        startReveal = true
    }

    val revealAnim by animateFloatAsState(
        targetValue = if (startReveal) 1f else 0f,
        animationSpec = tween(durationMillis = 1200, easing = CubicBezierEasing(0.2f, 1f, 0.3f, 1f)),
        label = "reveal"
    )

    val translateY by animateDpAsState(
        targetValue = if (startReveal) 0.dp else 20.dp,
        animationSpec = tween(durationMillis = 1200, easing = CubicBezierEasing(0.2f, 1f, 0.3f, 1f)),
        label = "translateY"
    )

    // Loading Bar Animation
    val infiniteTransition = rememberInfiniteTransition(label = "loading")
    val loadingOffset by infiniteTransition.animateFloat(
        initialValue = -0.3f,
        targetValue = 1.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "loadingOffset"
    )

    LaunchedEffect(uiState.navigateTo) {
        uiState.navigateTo?.let { destination ->
            delay(1200)
            when (destination) {
                NavigateTo.MAIN -> onNavigateToMain()
                NavigateTo.LOGIN -> onNavigateToSignin()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        // Subtle Background Atmosphere
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .offset(x = (-50).dp, y = (-50).dp)
                    .size(300.dp)
                    .blur(120.dp)
                    .background(colorScheme.primary.copy(alpha = 0.05f))
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 50.dp, y = 50.dp)
                    .size(400.dp)
                    .blur(150.dp)
                    .background(colorScheme.primary.copy(alpha = 0.05f))
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .offset(y = translateY)
                .alpha(revealAnim)
        ) {
            // Luxury Shopping Bag Icon
            Box(contentAlignment = Alignment.Center) {
                // Glow effect
                Icon(
                    painter = painterResource(id = R.drawable.baseline_shopping_bag_24),
                    contentDescription = null,
                    modifier = Modifier
                        .size(100.dp)
                        .blur(20.dp),
                    tint = colorScheme.primary.copy(alpha = 0.3f)
                )
                Icon(
                    painter = painterResource(id = R.drawable.baseline_shopping_bag_24),
                    contentDescription = "Cartify",
                    modifier = Modifier.size(100.dp),
                    tint = colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Brand Headline
            Text(
                text = "CARTIFY",
                style = MaterialTheme.typography.displayLarge.copy(
                    fontFamily = PlayfairDisplay,
                    fontWeight = FontWeight.Normal,
                    letterSpacing = 8.sp,
                    color = colorScheme.primary
                )
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Loading Indicator
            Box(
                modifier = Modifier
                    .width(140.dp)
                    .height(1.dp)
                    .background(colorScheme.primary.copy(alpha = 0.1f))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(0.3f)
                        .offset(x = 140.dp * loadingOffset)
                        .background(colorScheme.primary)
                )
            }
        }

        // Bottom Tagline
        Text(
            text = "CURATED LUXURY EXPERIENCE",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
                .alpha(revealAnim),
            style = MaterialTheme.typography.labelMedium.copy(
                fontFamily = HankenGrotesk,
                letterSpacing = 4.sp,
                color = colorScheme.onSurface.copy(alpha = 0.4f)
            )
        )
    }
}
