package com.example.brewbuddy.view

import android.app.Activity
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.brewbuddy.ui.theme.BrewBuddyTheme
import com.example.brewbuddy.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Make content fullscreen edge-to-edge

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
    val authViewModel: AuthViewModel = viewModel()

    // Collect user and error state from AuthViewModel
    val user by authViewModel.user.collectAsState()
    val authError by authViewModel.authError.collectAsState()

    // UI state - input fields, visibility, loading
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Animation state for card appearance
    var cardAppeared by remember { mutableStateOf(false) }
    val cardScale by animateFloatAsState(
        targetValue = if (cardAppeared) 1f else 0.7f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
    )
    val cardAlpha by animateFloatAsState(
        targetValue = if (cardAppeared) 1f else 0f,
        animationSpec = tween(durationMillis = 700, easing = LinearEasing)
    )

    // On user login success, navigate to dashboard and finish this activity
    LaunchedEffect(user) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (user != null && firebaseUser != null) {
            isLoading = false
            context.startActivity(Intent(context, DashboardActivity::class.java))
            (context as? Activity)?.finish()
        }
    }

    // In your LoginActivity.kt (or ViewModel, preferred), after login:
    val uid = FirebaseAuth.getInstance().currentUser?.uid
    if (uid != null) {
        FirebaseDatabase.getInstance().getReference("users").child(uid)
            .get().addOnSuccessListener { dataSnapshot ->
                val userProfile = dataSnapshot.getValue(Map::class.java)
                // Use userProfile data as needed (e.g. username, email)
            }
    }


    // Reset loading when authentication error received
    LaunchedEffect(authError) {
        if (!authError.isNullOrEmpty()) {
            isLoading = false
        }
    }

    // Play card appearance animation at startup
    LaunchedEffect(Unit) {
        cardAppeared = true
    }

    val isCredentialsEntered = email.isNotBlank() && password.isNotBlank()
    val buttonAlpha by animateFloatAsState(
        targetValue = if (isCredentialsEntered) 1f else 0.8f,
        animationSpec = tween(durationMillis = 400)
    )
    val buttonScale by animateFloatAsState(
        targetValue = if (isCredentialsEntered) 1f else 0.99f,
        animationSpec = tween(durationMillis = 400)
    )
    val loginContentColor = if (isCredentialsEntered) Color.White else Color(0xFFE66111)

    // Logo steam vertical animation (optional aesthetic)
    val infiniteTransition = rememberInfiniteTransition()
    val offsetY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = -16f,
        animationSpec = infiniteRepeatable(tween(1500, easing = LinearEasing), RepeatMode.Reverse)
    )

    // Background floating cups animation offset (optional)
    val cupBgOffset by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 800f,
        animationSpec = infiniteRepeatable(tween(22000, easing = LinearEasing), RepeatMode.Restart)
    )

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

    // Main container with background and floating cups
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
        // Background cups animation
        cupSpecs.forEachIndexed { i, (size, alpha, alignment) ->
            Icon(
                Icons.Default.Coffee,
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

        // Column with login UI
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Animated logo card
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
                        Icons.Default.Coffee,
                        contentDescription = "Coffee Mug Logo",
                        modifier = Modifier.size(60.dp),
                        tint = Color(0xFF8B4513)
                    )
                }
            }

            Spacer(Modifier.height(22.dp))

            Text(
                "BrewBuddy Login",
                fontSize = 34.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFD2691E)
            )
            Text(
                "Your Coffee Journal",
                fontSize = 16.sp,
                color = Color(0xFF8B4513).copy(alpha = 0.73f)
            )

            Spacer(Modifier.height(36.dp))

            // Login form card with opening animation
            Card(
                shape = RoundedCornerShape(40.dp),
                elevation = CardDefaults.cardElevation(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .graphicsLayer {
                        alpha = cardAlpha
                        scaleX = cardScale
                        scaleY = cardScale
                    },
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.97f))
            ) {
                Column(
                    Modifier.padding(28.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Welcome Back!",
                        fontSize = 23.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF381911),
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Email input
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email", color = Color(0xFFD2691E)) },
                        leadingIcon = {
                            Icon(Icons.Default.Email, contentDescription = "Email Icon", tint = Color(0xFF8B4513))
                        },
                        textStyle = MaterialTheme.typography.bodyLarge.copy(
                            color = Color(0xFF422010),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD2691E),
                            unfocusedBorderColor = Color(0xFF8B4513),
                            focusedLabelColor = Color(0xFFD2691E),
                            unfocusedLabelColor = Color(0xFF8B4513),
                            cursorColor = Color(0xFFD2691E),
                            focusedLeadingIconColor = Color(0xFFD2691E),
                            focusedTextColor = Color(0xFFD2691E),
                            unfocusedTextColor = Color(0xFF4D2D18)
                        )
                    )

                    // Password input
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password", color = Color(0xFFD2691E)) },
                        leadingIcon = {
                            Icon(Icons.Default.Lock, contentDescription = "Password Icon", tint = Color(0xFF8B4513))
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
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                        shape = RoundedCornerShape(15.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFFD2691E),
                            unfocusedBorderColor = Color(0xFF8B4513),
                            focusedLabelColor = Color(0xFFD2691E),
                            unfocusedLabelColor = Color(0xFF8B4513),
                            cursorColor = Color(0xFFD2691E),
                            focusedLeadingIconColor = Color(0xFFD2691E),
                            focusedTextColor = Color(0xFFD2691E),
                            unfocusedTextColor = Color(0xFF4D2D18)
                        )
                    )

                    Spacer(Modifier.height(18.dp))

                    // Login Button
                    val isCredentialsEntered = email.isNotBlank() && password.isNotBlank()
                    val buttonAlpha by animateFloatAsState(
                        targetValue = if (isCredentialsEntered) 1f else 0.8f,
                        animationSpec = tween(durationMillis = 400)
                    )
                    val buttonScale by animateFloatAsState(
                        targetValue = if (isCredentialsEntered) 1f else 0.99f,
                        animationSpec = tween(durationMillis = 400)
                    )
                    val loginContentColor = if (isCredentialsEntered) Color.White else Color(0xFFE66111)
                    Button(
                        onClick = {
                            isLoading = true
                            authViewModel.login(email.trim(), password.trim())
                        },
                        enabled = isCredentialsEntered && !isLoading,
                        shape = RoundedCornerShape(15.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.72f)
                            .height(44.dp)
                            .graphicsLayer {
                                alpha = buttonAlpha
                                scaleX = buttonScale
                                scaleY = buttonScale
                            },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFFD2691E),
                            disabledContainerColor = Color(0xFFD2691E).copy(alpha = 0.6f)
                        )
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
                                    Icons.Default.Login,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = loginContentColor
                                )
                                Spacer(Modifier.width(8.dp))
                                Text(
                                    "Login",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = loginContentColor
                                )
                            }
                        }
                    }

                    // Show authentication error if any
                    if (!authError.isNullOrEmpty()) {
                        Spacer(Modifier.height(12.dp))
                        Text(
                            text = authError ?: "",
                            color = Color.Red,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))

                    // Navigate to Registration screen
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Don't have an account?",
                            color = Color(0xFF8B4513),
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(Modifier.width(7.dp))
                        TextButton(onClick = {
                            context.startActivity(Intent(context, RegistrationActivity::class.java))
                            (context as? Activity)?.finish()
                        }) {
                            Text(
                                "Register",
                                color = Color(0xFFD2691E),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(Modifier.height(8.dp))
                }
            }
        }
    }
}

//private var isLoading by mutableStateOf(false)
