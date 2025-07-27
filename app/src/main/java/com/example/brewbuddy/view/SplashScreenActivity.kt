package com.example.brewbuddy.view

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.brewbuddy.R
import kotlinx.coroutines.delay

class SplashScreenActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SplashBody()
        }
    }
}

@Composable
fun SplashBody() {
    val context = LocalContext.current
    val activity = context as Activity

    // SharedPrefs to check login
    val sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE)
    val localEmail: String? = sharedPreferences.getString("email", "")

    // Animation state for logo (fade in)
    var logoAlpha by remember { mutableStateOf(0f) }

    // Splash duration and animation
    LaunchedEffect(Unit) {
        // Animate logo in over 800ms
        androidx.compose.animation.core.animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = androidx.compose.animation.core.tween(durationMillis = 800)
        ) { value, _ -> logoAlpha = value }

        // Hold splash for 3 seconds total

        context.startActivity(Intent(context, LoginActivity::class.java))


        activity.finish()
    }

    // UI layout
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Background image
        Image(
            painter = painterResource(R.drawable.splash_bg),
            contentDescription = "Splash Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = androidx.compose.ui.layout.ContentScale.Crop
        )
        // Dark overlay for readability/aesthetic
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.50f))
        )
        // Centered Logo
        Image(
            painter = painterResource(R.drawable.logo1),
            contentDescription = "App Logo",
            modifier = Modifier
                .align(Alignment.Center)
                .size(180.dp)
                .graphicsLayer { alpha = logoAlpha }
        )
    }
}


@Preview(showBackground = true)
@Composable
fun SplashBodyPreview() {
        SplashBody()
}
