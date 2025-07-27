package com.example.brewbuddy.view

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.brewbuddy.ui.theme.BrewBuddyTheme

class LoginActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BrewBuddyTheme {
                LoginBody()
            }
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginBody() {
    val context = LocalContext.current

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Opening animation for welcome card
    var cardAppeared by remember { mutableStateOf(false) }
    val cardScale by animateFloatAsState(
        targetValue = if (cardAppeared) 1f else 0.7f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing),
        label = "cardScale"
    )
    val cardAlpha by animateFloatAsState(
        targetValue = if (cardAppeared) 1f else 0f,
        animationSpec = tween(durationMillis = 700, easing = LinearEasing),
        label = "cardAlpha"
    )

    // Determine if credentials are entered
    val isCredentialsEntered = email.isNotBlank() && password.isNotBlank()

    // Animate Login Button alpha and scale based on input fields
    val buttonAlpha by animateFloatAsState(
        targetValue = if (isCredentialsEntered) 1f else 0.5f,
        animationSpec = tween(durationMillis = 400),
        label = "buttonAlpha"
    )
    val buttonScale by animateFloatAsState(
        targetValue = if (isCredentialsEntered) 1f else 0.95f,
        animationSpec = tween(durationMillis = 400),
        label = "buttonScale"
    )

    // Button content color: white if enabled, orange if not
    val loginContentColor = if (isCredentialsEntered) Color.White else Color(0xFFE66111)

    // Logo "steam" animation - vertical up/down
    val infiniteTransition = rememberInfiniteTransition(label = "login_logo_updown")
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -16f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "steam_updown"
    )

    // Background floating cups animation offset (for gentle vertical movement)
    val cupBgOffset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 800f,
        animationSpec = infiniteRepeatable(
            animation = tween(22000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "cup_float_bg"
    )

    LaunchedEffect(Unit) {
        cardAppeared = true
    }

    // Specifications for cups: size, alpha, alignment
    val cupSpecs = listOf(
        Triple(140.dp, 0.04f, Alignment.BottomStart),
        Triple(155.dp, 0.08f, Alignment.BottomEnd),
        Triple(170.dp, 0.12f, Alignment.TopStart),
        Triple(185.dp, 0.16f, Alignment.TopEnd),
        Triple(200.dp, 0.20f, Alignment.CenterStart),
        Triple(215.dp, 0.24f, Alignment.CenterEnd),
        Triple(230.dp, 0.28f, Alignment.TopCenter),
        Triple(245.dp, 0.32f, Alignment.BottomCenter)
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF2C1810),
                        Color(0xFF1A0F0A),
                        Color(0xFFFAF3E3)
                    )
                )
            )
    ) {
        // Background floating cups with vertical floating animation
        cupSpecs.forEachIndexed { i, (size, alpha, alignment) ->
            Icon(
                imageVector = Icons.Default.Coffee,
                contentDescription = null,
                tint = Color(0xFF8B4513).copy(alpha = alpha),
                modifier = Modifier
                    .size(size)
                    .offset(
                        x = (20 + 40 * i).dp,
                        y = (((cupBgOffset + 50 * i) % 800) - 250).dp
                    )
                    .align(alignment)
            )
        }



        // Main content column with your login UI
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated logo card - coffee mug with steam animation
            Card(
                shape = RoundedCornerShape(40),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.87f)),
                modifier = Modifier
                    .size(108.dp)
                    .offset(y = offsetY.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = Icons.Default.Coffee,
                        contentDescription = "Coffee Mug Logo",
                        modifier = Modifier.size(60.dp),
                        tint = Color(0xFF8B4513)
                    )
                }
            }
            Spacer(Modifier.height(22.dp))
            Text(
                text = "BrewBuddy Login",
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFD2691E)
            )
            Text(
                text = "Your Coffee Journal",
                fontSize = 16.sp,
                color = Color(0xFF8B4513).copy(alpha = 0.73f)
            )
            Spacer(Modifier.height(36.dp))

            // Main white card with opening animation & 10dp rounded corners
            Card(
                shape = RoundedCornerShape(10.dp),
                elevation = CardDefaults.cardElevation(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        scaleX = cardScale
                        scaleY = cardScale
                        alpha = cardAlpha
                    },
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.97f))
            ) {
                Column(
                    modifier = Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Welcome Back!",
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF381911),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email", color = Color(0xFFD2691E)) },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = "Email", tint = Color(0xFF8B4513))
                        },
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = Color(0xFF422010),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD2691E),
                            unfocusedBorderColor = Color(0xFF8B4513),
                            focusedLabelColor = Color(0xFFD2691E),
                            unfocusedLabelColor = Color(0xFF8B4513),
                            focusedLeadingIconColor = Color(0xFFD2691E),
                            cursorColor = Color(0xFFD2691E),
                            unfocusedTextColor = Color(0xFF4D2D18),
                            focusedTextColor = Color(0xFF381911)
                        )
                    )

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password", color = Color(0xFFD2691E)) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = "Password", tint = Color(0xFF8B4513))
                        },
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                    contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                    tint = Color(0xFFD2691E)
                                )
                            }
                        },
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = Color(0xFF422010),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD2691E),
                            unfocusedBorderColor = Color(0xFF8B4513),
                            focusedLabelColor = Color(0xFFD2691E),
                            unfocusedLabelColor = Color(0xFF8B4513),
                            focusedLeadingIconColor = Color(0xFFD2691E),
                            cursorColor = Color(0xFFD2691E),
                            unfocusedTextColor = Color(0xFF4D2D18),
                            focusedTextColor = Color(0xFF381911)
                        )
                    )

                    Spacer(modifier = Modifier.height(18.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            "Don't have an account?",
                            color = Color(0xFF8B4513),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.width(7.dp))
                        TextButton(
                            onClick = { /* Navigate to RegistrationActivity */ }
                        ) {
                            Text(
                                "Register",
                                color = Color(0xFFD2691E),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    TextButton(
                        onClick = { /* TODO: Forgot password */ },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            "Forgot Password?",
                            color = Color(0xFF8B4513),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Spacer(modifier = Modifier.height(18.dp))

                    // Login button with changing text/icon visibility/color
                    Button(
                        onClick = {
                            isLoading = true
                            // Your login logic here
                        },
                        modifier = Modifier
                            .fillMaxWidth(0.72f)
                            .height(44.dp)
                            .graphicsLayer {
                                alpha = buttonAlpha
                                scaleX = buttonScale
                                scaleY = buttonScale
                            },
                        shape = RoundedCornerShape(15.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD2691E),
                            disabledContainerColor = Color(0xFFD2691E).copy(alpha = 0.6f)
                        ),
                        enabled = isCredentialsEntered && !isLoading
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                color = loginContentColor,
                                modifier = Modifier.size(22.dp)
                            )
                        } else {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Login,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = loginContentColor
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Login",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = loginContentColor
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
