package com.example.brewbuddy.view

import android.app.Activity
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.brewbuddy.model.BrewData
import com.example.brewbuddy.viewmodel.AuthViewModel
import com.example.brewbuddy.viewmodel.BrewViewModel
import com.google.firebase.auth.FirebaseAuth
import kotlin.jvm.java
import kotlin.ranges.rangeTo
import kotlin.text.isBlank
import kotlin.text.isNotBlank
import kotlin.text.toDoubleOrNull
import kotlin.toString

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: BrewViewModel = viewModel(),
    onLogout: () -> Unit = {}
) {
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF2C1810), Color(0xFF1A0F0A), Color(0xFFFAF3E3))
    )
    val textColorPrimary = Color(0xFFD2691E)
    val textColorSecondary = Color(0xFF704214)
    val cardBackground = Color(0xFFF8E5D6)
    val cardTextColor = Color(0xFF2C1810)
    val fabColor = Color(0xFFD2691E)

    var menuExpanded by remember { mutableStateOf(false) }

    val authViewModel: AuthViewModel = viewModel()
    val user by authViewModel.user.collectAsState()
    val context = LocalContext.current

    // 1. Load brews for current user
    LaunchedEffect(user) {
        val uid = user?.uid
        if (uid != null) {
            viewModel.loadBrews(uid)
        }
    }

    // 2. Redirect to login if not authenticated
    LaunchedEffect(user) {
        val firebaseUser = FirebaseAuth.getInstance().currentUser
        if (user == null && firebaseUser == null) {
            context.startActivity(Intent(context, LoginActivity::class.java))
            (context as? Activity)?.finish()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Your Brews", color = textColorPrimary) },
                colors = TopAppBarDefaults.mediumTopAppBarColors(containerColor = Color(0xFF381911)),
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu", tint = textColorPrimary)
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier
                                        .background(
                                            color = Color(0xFFD2691E),
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                ) {
                                    Icon(Icons.Default.Logout, contentDescription = "Logout", tint = Color.White, modifier = Modifier.size(22.dp))
                                    Spacer(Modifier.width(8.dp))
                                    Text("Logout", color = Color.White, fontSize = 16.sp)
                                }
                            },
                            onClick = {
                                menuExpanded = false
                                authViewModel.logout()
                                onLogout()
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onAddBrew() },
                containerColor = fabColor
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = "Add Brew",
                    tint = Color.White
                )
            }
        },
        containerColor = Color.Transparent,
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .navigationBarsPadding()
                .fillMaxSize()
                .background(backgroundGradient)
        ) {
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                if (viewModel.brews.isEmpty()) {
                    Text(
                        "No brews yet. Click + to add your first brew!",
                        color = textColorPrimary,
                        style = MaterialTheme.typography.headlineSmall
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(viewModel.brewsSorted) { brew ->
                            BrewItem(
                                brew = brew,
                                onEdit = { viewModel.onEditBrew(brew) },
                                onDelete = { viewModel.onDeleteBrew(brew) },
                                onClick = { viewModel.onShowPopup(brew) },
                                cardBackground = cardBackground,
                                cardTextColor = cardTextColor,
                                cardSecondaryColor = textColorSecondary
                            )
                        }
                    }
                }
            }

            // Show Brew details popup
            if (viewModel.selectedBrew != null) {
                NotePopup(note = viewModel.selectedBrew, onDismiss = { viewModel.onDismissPopup() })
            }

            // Show add/edit Brew dialog
            if (viewModel.showEditDialog) {
                BrewDialog(
                    initialData = viewModel.editingBrew ?: BrewData(key = null, name = "", notes = "", rating = 0.0),
                    onDismiss = { viewModel.onDismissDialog() },
                    onSave = { viewModel.onSaveBrew(it) },
                    textColorPrimary = textColorPrimary,
                    textColorSecondary = textColorSecondary,
                    backgroundColor = cardBackground
                )
            }
        }
    }
}

@Composable
fun BrewItem(
    brew: BrewData,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit,
    cardBackground: Color,
    cardTextColor: Color,
    cardSecondaryColor: Color
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(16.dp))
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = cardBackground),
        elevation = CardDefaults.cardElevation(8.dp),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, cardSecondaryColor.copy(alpha = 0.15f))
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Coffee,
                contentDescription = null,
                tint = cardSecondaryColor,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = brew.name, color = cardTextColor, fontSize = 18.sp)
                Text(text = brew.notes, maxLines = 1, color = cardSecondaryColor, fontSize = 14.sp)
                Text(text = "Rating: ${brew.rating}", fontSize = 13.sp, color = cardSecondaryColor)
            }
            IconButton(onClick = onEdit) {
                Icon(Icons.Default.Edit, contentDescription = "Edit Brew", tint = cardSecondaryColor)
            }
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete Brew", tint = Color(0xFFD32F2F))
            }
        }
    }
}

@Composable
fun NotePopup(note: BrewData?, onDismiss: () -> Unit) {
    if (note != null) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF733708))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp)
                ) {
                    Text(
                        text = note.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = note.notes,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Rating: ${note.rating}",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = onDismiss, modifier = Modifier.align(Alignment.End)) {
                        Text("Close")
                    }
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrewDialog(
    initialData: BrewData,
    onDismiss: () -> Unit,
    onSave: (BrewData) -> Unit,
    textColorPrimary: Color,
    textColorSecondary: Color,
    backgroundColor: Color
) {
    var name by remember { mutableStateOf(initialData.name) }
    var notes by remember { mutableStateOf(initialData.notes) }
    var rating by remember { mutableStateOf(initialData.rating.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val ratingDouble = rating.toDoubleOrNull() ?: -1.0
                    if (name.isNotBlank() && ratingDouble in 0.0..5.0) {
                        onSave(BrewData(key = initialData.key, name = name, notes = notes, rating = ratingDouble))
                    }
                }
            ) {
                Text("Save", color = textColorPrimary)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = textColorPrimary)
            }
        },
        title = {
            Text(if (initialData.name.isBlank()) "Add Brew" else "Edit Brew", color = textColorPrimary)
        },
        text = {
            Column {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Brew Name", color = textColorPrimary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = textColorPrimary,
                        unfocusedBorderColor = textColorSecondary,
                        focusedLabelColor = textColorPrimary,
                        unfocusedLabelColor = textColorSecondary,
                        cursorColor = textColorPrimary,
                        focusedLeadingIconColor = textColorPrimary,
                        focusedTextColor = textColorPrimary,
                        unfocusedTextColor = textColorSecondary
                    )
                )
                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes", color = textColorPrimary) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = textColorPrimary,
                        unfocusedBorderColor = textColorSecondary,
                        focusedLabelColor = textColorPrimary,
                        unfocusedLabelColor = textColorSecondary,
                        cursorColor = textColorPrimary,
                        focusedLeadingIconColor = textColorPrimary,
                        focusedTextColor = textColorPrimary,
                        unfocusedTextColor = textColorSecondary
                    )
                )
                OutlinedTextField(
                    value = rating,
                    onValueChange = { rating = it },
                    label = { Text("Rating (0 to 5)", color = textColorPrimary) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions.Default.copy(
                        keyboardType = KeyboardType.Number
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = textColorPrimary,
                        unfocusedBorderColor = textColorSecondary,
                        focusedLabelColor = textColorPrimary,
                        unfocusedLabelColor = textColorSecondary,
                        cursorColor = textColorPrimary,
                        focusedLeadingIconColor = textColorPrimary,
                        focusedTextColor = textColorPrimary,
                        unfocusedTextColor = textColorSecondary
                    )
                )
            }
        },
        containerColor = backgroundColor
    )
}

