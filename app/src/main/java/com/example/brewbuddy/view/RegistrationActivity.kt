package com.example.brewbuddy.view

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brewbuddy.ui.theme.BrewBuddyTheme

class RegistrationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BrewBuddyTheme {
                RegistrationBody()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationBody() {
    val context = LocalContext.current

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var cardAnimationStarted by remember { mutableStateOf(false) }

    // Entrance animation for card
    val cardScale by animateFloatAsState(
        targetValue = if (cardAnimationStarted) 1f else 0.7f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing), label = "scaleAnim"
    )
    val cardAlpha by animateFloatAsState(
        targetValue = if (cardAnimationStarted) 1f else 0f,
        animationSpec = tween(700, easing = LinearEasing), label = "alphaAnim"
    )
    // Start animation on first draw
    LaunchedEffect(Unit) { cardAnimationStarted = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF2C1810),
                        Color(0xFF1A0F0A),
                        Color(0xFFFAF3E3)
                    )
                )
            )
    ) {
        Column(
            Modifier
                .fillMaxSize()
                .padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedBaristaMugHeader()
            Spacer(Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = cardScale
                        scaleY = cardScale
                        alpha = cardAlpha
                    }
                    .fillMaxWidth()
                    .shadow(12.dp, RoundedCornerShape(23.dp)),
                shape = RoundedCornerShape(23.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.99f))
            ) {
                Column(
                    Modifier.padding(24.dp, 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Create your BrewBuddy Account",
                        fontWeight = FontWeight.Black,
                        color = Color(0xFFD2691E),
                        fontSize = 23.sp
                    )
                    Spacer(Modifier.height(15.dp))

                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username", color = Color(0xFFD2691E)) },
                        leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = Color(0xFF8B4513)) },
                        shape = RoundedCornerShape(13.dp),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD2691E),
                            unfocusedBorderColor = Color(0xFF8B4513),
                            focusedLeadingIconColor = Color(0xFFD2691E),

                        )
                    )
                    Spacer(Modifier.height(11.dp))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email", color = Color(0xFFD2691E)) },
                        leadingIcon = { Icon(Icons.Filled.Email, contentDescription = null, tint = Color(0xFF8B4513)) },
                        shape = RoundedCornerShape(13.dp),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD2691E),
                            unfocusedBorderColor = Color(0xFF8B4513),
                            focusedLeadingIconColor = Color(0xFFD2691E),
                        )
                    )
                    Spacer(Modifier.height(11.dp))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password", color = Color(0xFFD2691E)) },
                        leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = Color(0xFF8B4513)) },
                        trailingIcon = {
                            IconButton(onClick = { showPassword = !showPassword }) {
                                Icon(
                                    if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = null,
                                    tint = Color(0xFFD2691E)
                                )
                            }
                        },
                        shape = RoundedCornerShape(13.dp),
                        singleLine = true,
                        visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD2691E),
                            unfocusedBorderColor = Color(0xFF8B4513),
                            focusedLeadingIconColor = Color(0xFFD2691E),
                        )
                    )
                    Spacer(Modifier.height(11.dp))
                    OutlinedTextField(
                        value = confirmPassword,
                        onValueChange = { confirmPassword = it },
                        label = { Text("Confirm Password", color = Color(0xFFD2691E)) },
                        leadingIcon = { Icon(Icons.Filled.VerifiedUser, contentDescription = null, tint = Color(0xFF8B4513)) },
                        trailingIcon = {
                            IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                                Icon(
                                    if (showConfirmPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                    contentDescription = null,
                                    tint = Color(0xFFD2691E)
                                )
                            }
                        },
                        shape = RoundedCornerShape(13.dp),
                        singleLine = true,
                        visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD2691E),
                            unfocusedBorderColor = Color(0xFF8B4513),
                            focusedLeadingIconColor = Color(0xFFD2691E),
                        )
                    )
                    Spacer(Modifier.height(18.dp))
                    Button(
                        onClick = {
                            isLoading = true
                            // TODO: Your registration logic

                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD2691E),
                            contentColor = Color.White
                        ),
                        enabled = !isLoading,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(45.dp)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        } else {
                            Text("Register", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                    TextButton(onClick = {
                        context.startActivity(Intent(context, LoginActivity::class.java))
                        (context as Activity).finish()
                    }) {
                        Text(
                            "Already have an account? Sign in",
                            color = Color(0xFFD2691E),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnimatedBaristaMugHeader() {
    // Arm raising a cup of steaming coffee, with cup bob and animated steam lines
    val infiniteTransition = rememberInfiniteTransition(label = "baristaMug")
    val mugOffsetY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mugUpdown"
    )

    val steamAlpha by infiniteTransition.animateFloat(
        initialValue = 0.58f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "steamAlpha"
    )

    Box(Modifier.size(120.dp), contentAlignment = Alignment.Center) {
        // Arm
        Box(
            modifier = Modifier
                .width(32.dp)
                .height(52.dp)
                .background(Color(0xFFD2691E), RoundedCornerShape(13.dp))
                .align(Alignment.BottomCenter)
                .offset(x = 28.dp, y = 24.dp)
        )
        // Cup with steam
        Box(
            modifier = Modifier
                .size(37.dp, 38.dp)
                .align(Alignment.Center)
                .offset(y = mugOffsetY.dp)
        ) {
            // Cup body
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(19.dp)
                    .background(Color.White, RoundedCornerShape(6.dp))
            )
            // Coffee inside cup
            Box(
                modifier = Modifier
                    .padding(6.dp, 4.dp, 6.dp, 13.dp)
                    .fillMaxWidth()
                    .height(8.dp)
                    .background(Color(0xFF795548), RoundedCornerShape(99))
            )
            // Steam lines
            Box(
                modifier = Modifier
                    .offset(x = 10.dp, y = (-7).dp)
                    .width(4.dp)
                    .height(16.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFFE8E1CE).copy(alpha = steamAlpha), Color.Transparent)
                        ),
                        RoundedCornerShape(50)
                    )
            )
            Box(
                modifier = Modifier
                    .offset(x = 18.dp, y = (-9).dp)
                    .width(4.dp)
                    .height(13.dp)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(Color(0xFFE8E1CE).copy(alpha = steamAlpha * 0.72f), Color.Transparent)
                        ),
                        RoundedCornerShape(50)
                    )
            )
            // Cup handle
            Box(
                modifier = Modifier
                    .offset(x = 27.dp, y = 9.dp)
                    .size(9.dp)
                    .background(Color(0xFFD2691E), shape = RoundedCornerShape(90))
            )
        }
    }
}
