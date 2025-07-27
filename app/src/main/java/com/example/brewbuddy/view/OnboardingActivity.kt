package com.example.brewbuddy.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.brewbuddy.ui.theme.BrewBuddyTheme
import com.example.brewbuddy.ui.theme.CoffeeBrown
import com.example.brewbuddy.ui.theme.CoffeeCream
import com.example.brewbuddy.R

class OnboardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BrewBuddyTheme {
                OnboardingScreen(
                    onFinish = {
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun OnboardingScreen(onFinish: () -> Unit) {
    val slides = listOf(
        OnboardingData(
            title = "Welcome to BrewBuddy!",
            subtitle = "Capture every cup and savor your coffee journey.",
            imageRes = R.drawable.login_bg // Replace with your onboarding images if any
        ),
        OnboardingData(
            title = "Record Brews",
            subtitle = "Log your favorite beans, notes, and ratings easily.",
            imageRes = R.drawable.login_bg
        ),
        OnboardingData(
            title = "Start Your Journal",
            subtitle = "Begin exploring and reflecting on your brews!",
            imageRes = R.drawable.login_bg
        )
    )

    var currentIndex by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(CoffeeCream),
        contentAlignment = Alignment.Center
    ) {
        AnimatedContent(
            targetState = currentIndex,
            transitionSpec = {
                // Slide animation between slides
                slideInHorizontally(
                    initialOffsetX = { fullWidth -> fullWidth },
                    animationSpec = tween(300)
                ) + fadeIn(animationSpec = tween(300)) with
                        slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = tween(300)
                        ) + fadeOut(animationSpec = tween(300))
            }
        ) { targetIndex ->
            val slide = slides[targetIndex]
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(24.dp)
            ) {
                Image(
                    painter = painterResource(id = slide.imageRes),
                    contentDescription = "Onboarding Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = slide.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = CoffeeBrown
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = slide.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = CoffeeBrown
                )

                Spacer(modifier = Modifier.height(48.dp))

                Button(
                    onClick = {
                        if (currentIndex < slides.size - 1) {
                            currentIndex += 1
                        } else {
                            onFinish()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = if (currentIndex < slides.size - 1) "Next" else "Start")
                }
            }
        }
    }
}

data class OnboardingData(
    val title: String,
    val subtitle: String,
    val imageRes: Int
)
