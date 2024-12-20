package com.example.firebaseauthdemo.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreClass {
    private val db = FirebaseFirestore.getInstance()

    // Add user data to Firestore
    suspend fun addUserData(userId: String, userData: Map<String, Any>) {
        db.collection("users").document(userId).set(userData).await()
    }

    // Update user data in Firestore (creates the document if it doesn't exist)
    suspend fun updateUserData(userId: String, userData: Map<String, Any>) {
        val documentRef = db.collection("users").document(userId)
        val documentSnapshot = documentRef.get().await()

        if (documentSnapshot.exists()) {
            // Document exists, perform update
            documentRef.update(userData).await()
        } else {
            // Document doesn't exist, create it
            documentRef.set(userData).await()
        }
    }

    // Load user data from Firestore
    suspend fun loadUserData(userId: String): Map<String, Any>? {
        return db.collection("users").document(userId).get().await().data
    }
}
