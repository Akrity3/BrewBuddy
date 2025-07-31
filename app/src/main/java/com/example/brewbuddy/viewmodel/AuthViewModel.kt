package com.example.brewbuddy.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.brewbuddy.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.google.firebase.database.FirebaseDatabase
import kotlin.onFailure
import kotlin.text.get

class AuthViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    // Holds the current user; null means not authenticated.
    private val _user = MutableStateFlow<FirebaseUser?>(null)
    val user = _user.asStateFlow()

    // Holds authentication operation error text, if any.
    private val _authError = MutableStateFlow<String?>(null)
    val authError = _authError.asStateFlow()

    // Holds the user's profile (username, email, etc.) loaded from Realtime Database.
    private val _userProfile = MutableStateFlow<Map<String, Any>?>(null)
    val userProfile = _userProfile.asStateFlow()

    init {
        // Listen for authentication state changes (login/logout/session restore).
        repository.addAuthStateChangedListener { firebaseUser ->
            _user.value = firebaseUser

            if (firebaseUser != null) {
                // Load user profile when a user is signed in
                loadUserProfile(firebaseUser.uid)
            } else {
                // User logged out - clear profile and errors
                _userProfile.value = null
                _authError.value = null
            }
        }
    }

    // Loads the user's profile from Firebase Realtime Database
    fun loadUserProfile(uid: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("users").child(uid)
        dbRef.get()
            .addOnSuccessListener { dataSnapshot ->
                val profile = dataSnapshot.value as? Map<String, Any>
                _userProfile.value = profile
            }
            .addOnFailureListener { exception ->
                _authError.value = "Failed to load profile: ${exception.localizedMessage}"
                _userProfile.value = null
            }
    }

    fun register(email: String, password: String) {
        viewModelScope.launch {
            val result = repository.registerWithEmail(email, password)
            result.onFailure { _authError.value = it.localizedMessage }
            // On success, AuthStateListener will update user and load profile automatically
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            val result = repository.loginWithEmail(email, password)
            result.onFailure { _authError.value = it.localizedMessage }
            // On success, AuthStateListener will update user and load profile automatically
        }
    }

    fun logout() {
        repository.logout()
        // AuthStateListener will handle clearing user and profile state
    }
}
