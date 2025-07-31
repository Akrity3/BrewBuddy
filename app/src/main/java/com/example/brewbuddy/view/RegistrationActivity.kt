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
import androidx.compose.ui.draw.shadow
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
    val authViewModel: AuthViewModel = viewModel()

    // Input fields state
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Authentication error message
    val authError by authViewModel.authError.collectAsState()

    // Animated appearance for the card
    var cardAnimationStarted by remember { mutableStateOf(false) }
    val cardScale by animateFloatAsState(
        targetValue = if (cardAnimationStarted) 1f else 0.7f,
        animationSpec = tween(durationMillis = 700, easing = FastOutSlowInEasing)
    )
    val cardAlpha by animateFloatAsState(
        targetValue = if (cardAnimationStarted) 1f else 0f,
        animationSpec = tween(durationMillis = 700, easing = LinearEasing)
    )

    // Start animation on first composition
    LaunchedEffect(Unit) {
        cardAnimationStarted = true
    }

    // Enable button only if inputs are valid
    val isInputValid = username.isNotBlank() && email.isNotBlank() && password.isNotBlank() &&
            confirmPassword.isNotBlank() && password == confirmPassword

    // Observe user state; If registered successfully, navigate to LoginActivity
    val user by authViewModel.user.collectAsState()
    LaunchedEffect(user) {
        // When a new user object comes in after registration, save user profile, then sign out, then navigate
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (firebaseUser != null) {
            // Save user profile to Realtime Database
            val db = FirebaseDatabase.getInstance().reference
            val userId = firebaseUser.uid
            val profileMap = mapOf(
                "uid" to userId,
                "username" to username.trim(),
                "email" to email.trim()
            )
            db.child("users").child(userId).setValue(profileMap).addOnCompleteListener {

                // Immediately sign out newly registered user
                FirebaseAuth.getInstance().signOut()

                // Go to Login after sign out (forcing manual login)
                isLoading = false
                context.startActivity(Intent(context, LoginActivity::class.java))
                (context as? Activity)?.finish()
            }
        }
    }


    // Reset loading state when auth error changes
    LaunchedEffect(authError) {
        if (!authError.isNullOrEmpty()) {
            isLoading = false
        }
    }

    Column(
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
            .padding(18.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animated header or logo can be placed here (optional)

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
                    text = "Create your BrewBuddy Account",
                    fontWeight = FontWeight.Black,
                    color = Color(0xFFD2691E),
                    fontSize = 23.sp
                )
                Spacer(Modifier.height(15.dp))

                // Username input
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username", color = Color(0xFFD2691E)) },
                    leadingIcon = {
                        Icon(Icons.Filled.Person, contentDescription = "Username", tint = Color(0xFF8B4513))
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(13.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFD2691E),
                        unfocusedBorderColor = Color(0xFF8B4513),
                        focusedLeadingIconColor = Color(0xFFD2691E),
                        focusedLabelColor = Color(0xFFD2691E),
                        unfocusedLabelColor = Color(0xFF8B4513),
                        cursorColor = Color(0xFFD2691E)
                    )
                )
                Spacer(Modifier.height(11.dp))

                // Email input
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email", color = Color(0xFFD2691E)) },
                    leadingIcon = {
                        Icon(Icons.Filled.Email, contentDescription = "Email", tint = Color(0xFF8B4513))
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    shape = RoundedCornerShape(13.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFD2691E),
                        unfocusedBorderColor = Color(0xFF8B4513),
                        focusedLeadingIconColor = Color(0xFFD2691E),
                        focusedLabelColor = Color(0xFFD2691E),
                        unfocusedLabelColor = Color(0xFF8B4513),
                        cursorColor = Color(0xFFD2691E)
                    )
                )
                Spacer(Modifier.height(11.dp))

                // Password input
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Password", color = Color(0xFFD2691E)) },
                    leadingIcon = {
                        Icon(Icons.Filled.Lock, contentDescription = "Password", tint = Color(0xFF8B4513))
                    },
                    trailingIcon = {
                        IconButton(onClick = { showPassword = !showPassword }) {
                            Icon(
                                imageVector = if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (showPassword) "Hide password" else "Show password",
                                tint = Color(0xFFD2691E)
                            )
                        }
                    },
                    singleLine = true,
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(13.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFD2691E),
                        unfocusedBorderColor = Color(0xFF8B4513),
                        focusedLeadingIconColor = Color(0xFFD2691E),
                        focusedLabelColor = Color(0xFFD2691E),
                        unfocusedLabelColor = Color(0xFF8B4513),
                        cursorColor = Color(0xFFD2691E)
                    )
                )
                Spacer(Modifier.height(11.dp))

                // Confirm password input
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = { confirmPassword = it },
                    label = { Text("Confirm Password", color = Color(0xFFD2691E)) },
                    leadingIcon = {
                        Icon(Icons.Filled.VerifiedUser, contentDescription = "Confirm Password", tint = Color(0xFF8B4513))
                    },
                    trailingIcon = {
                        IconButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                            Icon(
                                imageVector = if (showConfirmPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility,
                                contentDescription = if (showConfirmPassword) "Hide password" else "Show password",
                                tint = Color(0xFFD2691E)
                            )
                        }
                    },
                    singleLine = true,
                    visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(13.dp),
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFD2691E),
                        unfocusedBorderColor = Color(0xFF8B4513),
                        focusedLeadingIconColor = Color(0xFFD2691E),
                        focusedLabelColor = Color(0xFFD2691E),
                        unfocusedLabelColor = Color(0xFF8B4513),
                        cursorColor = Color(0xFFD2691E)
                    )
                )

                Spacer(Modifier.height(18.dp))

                // Registration Button
                Button(
                    onClick = {
                        isLoading = true
                        authViewModel.register(email.trim(), password.trim())
                    },
                    enabled = isInputValid && !isLoading,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD2691E),
                        contentColor = Color.White
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(45.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(22.dp))
                    } else {
                        Text("Register", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    }
                }

                // Show auth error message if any
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

                // Navigate to Login screen if user already has an account
                TextButton(onClick = {
                    context.startActivity(Intent(context, LoginActivity::class.java))
                    (context as? Activity)?.finish()
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
