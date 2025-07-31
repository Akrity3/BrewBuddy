package com.example.brewbuddy.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.brewbuddy.ui.theme.BrewBuddyTheme
import com.example.brewbuddy.viewmodel.BrewViewModel

class DashboardActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BrewBuddyTheme {
                val viewModel: BrewViewModel = viewModel()
                DashboardScreen(
                    viewModel = viewModel,
                    onLogout = {
                        // Optionally handle additional logout logic here if needed
                    }
                )
            }
        }
    }
}

