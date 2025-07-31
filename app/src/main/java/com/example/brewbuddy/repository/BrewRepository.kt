package com.example.brewbuddy.repository

import androidx.compose.runtime.mutableStateListOf
import com.example.brewbuddy.model.BrewData
import com.google.firebase.database.*
import kotlin.collections.getValue
import kotlin.jvm.java
import kotlin.let

class BrewRepository {

    // Internal observable mutable list of brews for UI Composables
    private val _brews = mutableStateListOf<BrewData>()
    val brews: List<BrewData>
        get() = _brews

    private var brewsListener: ValueEventListener? = null
    private var brewsRef: DatabaseReference? = null

    /**
     * Loads brews for a specific user by listening to changes in Realtime Database.
     * Calls onLoaded callback once data is loaded or updated.
     */
    fun getBrewsForCurrentUser(uid: String, onLoaded: (() -> Unit)? = null) {
        brewsRef = FirebaseDatabase.getInstance().getReference("brews").child(uid)

        // Remove previous listener if any
        brewsListener?.let {
            brewsRef?.removeEventListener(it)
        }

        brewsListener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                _brews.clear()
                for (childSnapshot in snapshot.children) {
                    val brew = childSnapshot.getValue(BrewData::class.java)
                    brew?.key = childSnapshot.key  // Save Firebase key in BrewData
                    brew?.let { _brews.add(it) }
                }
                onLoaded?.invoke()
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
            }
        }

        brewsRef?.addValueEventListener(brewsListener as ValueEventListener)
    }

    // Adds a brew to the current user's brews list in database.
    fun addBrew(uid: String, brew: BrewData) {
        val ref = FirebaseDatabase.getInstance().getReference("brews").child(uid)
        ref.push().setValue(brew)
    }

    /**
     * Updates an existing brew by replacing oldBrew with newBrew.
     * Firebase Realtime Database needs a key, so this method assumes BrewData has an id/key.
     * You need to adjust BrewData model to include a 'key' field if not already present.
     */
    fun updateBrew(uid: String, brewKey: String, newBrew: BrewData) {
        val ref = FirebaseDatabase.getInstance().getReference("brews").child(uid).child(brewKey)
        ref.setValue(newBrew)
    }

    //Deletes a brew by its key.
    fun deleteBrew(uid: String, brewKey: String) {
        val ref = FirebaseDatabase.getInstance().getReference("brews").child(uid).child(brewKey)
        ref.removeValue()
    }

    // Clean up listener to avoid memory leaks.
}
