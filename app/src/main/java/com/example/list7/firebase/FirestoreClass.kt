package com.example.firebaseauthdemo.firebase

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreClass {
    private val db = FirebaseFirestore.getInstance()

    // Add user data to Firestore
    suspend fun addUserData(userId: String, userData: Map<String, Any>) {
        db.collection("users").document(userId).set(userData).await()
    }

    // Update user data in Firestore
    suspend fun updateUserData(userId: String, userData: Map<String, Any>) {
        db.collection("users").document(userId).update(userData).await()
    }

    // Load user data from Firestore
    suspend fun loadUserData(userId: String): Map<String, Any>? {
        return db.collection("users").document(userId).get().await().data
    }
}
