package com.example.brewbuddy.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    /** Observe authentication state via Firebase listener. */
    fun addAuthStateChangedListener(callback: (FirebaseUser?) -> Unit): FirebaseAuth.AuthStateListener {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            callback(auth.currentUser)
        }
        firebaseAuth.addAuthStateListener(listener)
        return listener
    }

    fun removeAuthStateChangedListener(listener: FirebaseAuth.AuthStateListener) {
        firebaseAuth.removeAuthStateListener(listener)
    }

    /** Register a new user with email and password */
    suspend fun registerWithEmail(email: String, password: String): Result<FirebaseUser> = try {
        val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
        Result.success(authResult.user!!)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /** Login user with email and password */
    suspend fun loginWithEmail(email: String, password: String): Result<FirebaseUser> = try {
        val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
        Result.success(authResult.user!!)
    } catch (e: Exception) {
        Result.failure(e)
    }

    /** Log out the current user */
    fun logout() {
        firebaseAuth.signOut()
    }
}
