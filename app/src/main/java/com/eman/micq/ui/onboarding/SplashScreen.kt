package com.eman.micq.ui.onboarding

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.eman.micq.ui.theme.MicQHeroGradient
import com.eman.micq.ui.theme.MicQTheme

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    SplashContent(
        onNavigateToLogin = onNavigateToLogin,
        onNavigateToRegister = onNavigateToRegister
    )
}

@Composable
fun SplashContent(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val scale by animateFloatAsState(
        targetValue = 1.2f,
        animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing),
        label = "LogoScale"
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MicQHeroGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Default.Mic,
                contentDescription = "MicQ Logo",
                modifier = Modifier
                    .size(120.dp)
                    .scale(scale),
                tint = MaterialTheme.colorScheme.onPrimary
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Professional Karaoke Management for Venues",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(48.dp))
            
            Button(
                onClick = onNavigateToRegister,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary,
                    contentColor = MaterialTheme.colorScheme.primary
                ),
                shape = CircleShape
            ) {
                Text("Create account", style = MaterialTheme.typography.titleLarge)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedButton(
                onClick = onNavigateToLogin,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimary),
                shape = CircleShape
            ) {
                Text("Log in", style = MaterialTheme.typography.titleLarge)
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    MicQTheme {
        SplashContent(
            onNavigateToLogin = {},
            onNavigateToRegister = {}
        )
    }
}
