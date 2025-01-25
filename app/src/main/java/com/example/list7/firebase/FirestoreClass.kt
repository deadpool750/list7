package com.example.firebaseauthdemo.firebase

import com.example.list7.Item
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreClass {
    private val db = FirebaseFirestore.getInstance()

    // Add or Update User Data
    suspend fun addUserData(userId: String, userData: Map<String, Any>) {
        db.collection("users").document(userId).set(userData).await()
    }

    suspend fun updateUserData(userId: String, userData: Map<String, Any>) {
        val documentRef = db.collection("users").document(userId)
        val documentSnapshot = documentRef.get().await()

        if (documentSnapshot.exists()) {
            documentRef.update(userData).await()
        } else {
            documentRef.set(userData).await()
        }
    }

    // Load User Data
    suspend fun loadUserData(userId: String): Map<String, Any>? {
        return db.collection("users").document(userId).get().await().data
    }

    // Delete User Data
    suspend fun deleteUserData(userId: String) {
        db.collection("users").document(userId).delete().await()
    }

    // Update User Balance
    suspend fun updateUserBalance(userId: String, newBalance: Int) {
        val userRef = db.collection("users").document(userId)
        userRef.update("balance", newBalance).await()
    }

    // Get User Balance
    suspend fun getUserBalance(userId: String): Int {
        val userRef = db.collection("users").document(userId)
        val snapshot = userRef.get().await()
        return snapshot.getLong("balance")?.toInt() ?: 0
    }

    // Add Item to a Firestore Collection
    fun addItem(
        collectionPath: String,
        item: Item,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection(collectionPath).add(item.toMap())
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    // Load Items from a Firestore Collection
    fun loadItems(
        collectionPath: String,
        onSuccess: (List<Item>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection(collectionPath).get()
            .addOnSuccessListener { querySnapshot ->
                val items = querySnapshot.documents.mapNotNull {
                    it.data?.let { data -> Item.fromMap(data) }
                }
                onSuccess(items)
            }
            .addOnFailureListener { onFailure(it) }
    }

    // Delete Items from a Firestore Collection
    suspend fun deleteItems(collectionPath: String, itemIds: List<String>) {
        val collectionRef = db.collection(collectionPath)
        for (itemId in itemIds) {
            collectionRef.document(itemId).delete().await()
        }
    }
}
