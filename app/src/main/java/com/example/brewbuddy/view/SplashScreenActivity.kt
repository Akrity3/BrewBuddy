package com.example.brewbuddy.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.brewbuddy.R
import com.google.firebase.auth.FirebaseAuth
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

    // Animation state for logo (fade in)
    var logoAlpha by remember { mutableStateOf(0f) }

    // Splash duration and animation & navigation
    LaunchedEffect(Unit) {

        // Animate logo in over 800ms
        androidx.compose.animation.core.animate(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = androidx.compose.animation.core.tween(durationMillis = 800)
        ) { value, _ -> logoAlpha = value }

        // Hold splash for ~3s total
        delay(2200)

        // Use FirebaseAuth for session check
        val firebaseUser = FirebaseAuth.getInstance().currentUser

        if (firebaseUser != null) {

            // User signed-in, go to Dashboard
            context.startActivity(Intent(context, DashboardActivity::class.java))
        } else {

            // No user, go to Login
            context.startActivity(Intent(context, LoginActivity::class.java))
        }
        activity.finish()  // Ensures SplashScreenActivity is cleared from backstack
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
                .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.60f))
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
