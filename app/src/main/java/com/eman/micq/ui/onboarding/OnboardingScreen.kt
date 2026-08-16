package com.eman.micq.ui.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.MicExternalOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.eman.micq.ui.theme.MicQHeroGradient
import com.eman.micq.ui.theme.MicQTheme
import kotlinx.coroutines.launch

data class OnboardingPage(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val roleName: String
)

@Composable
fun OnboardingScreen(onOnboardingFinished: () -> Unit) {
    val pages = listOf(
        OnboardingPage(
            title = "For Administrators",
            description = "Monitor venue performance, manage your staff team, and track loyal karaoke fans.",
            icon = Icons.Default.Settings,
            roleName = "ADMIN"
        ),
        OnboardingPage(
            title = "For DJs",
            description = "Control the live queue, manage singer rotations, and track your performance stats.",
            icon = Icons.Default.MicExternalOn,
            roleName = "DJ"
        ),
        OnboardingPage(
            title = "For Performers",
            description = "Easily add customers to the queue, manage table requests, and keep the show running.",
            icon = Icons.Default.Badge,
            roleName = "PERFORMER"
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MicQHeroGradient)
        ) {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { index ->
                OnboardingPageContent(page = pages[index])
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Page Indicator
                Row {
                    repeat(pages.size) { index ->
                        val color = if (pagerState.currentPage == index) 
                            MaterialTheme.colorScheme.onPrimary 
                        else 
                            MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.3f)
                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .size(8.dp)
                                .background(color, MaterialTheme.shapes.small)
                        )
                    }
                }

                Button(
                    onClick = {
                        if (pagerState.currentPage < pages.size - 1) {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            onOnboardingFinished()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(if (pagerState.currentPage == pages.size - 1) "GET STARTED" else "NEXT")
                }
            }
        }
    }
}

@Composable
fun OnboardingPageContent(page: OnboardingPage) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = page.icon,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.onPrimary
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = page.title,
            style = MaterialTheme.typography.displayLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = page.description,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.9f),
            textAlign = TextAlign.Center
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OnboardingScreenPreview() {
    MicQTheme {
        OnboardingScreen(onOnboardingFinished = {})
    }
}
