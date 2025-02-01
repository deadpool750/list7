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

    // Update User Balance (now accepts Double)
    suspend fun updateUserBalance(userId: String, newBalance: Double) {
        val userRef = db.collection("users").document(userId)
        userRef.update("balance", newBalance).await()
    }

    // Get User Balance
    suspend fun getUserBalance(userId: String): Double {
        val userRef = db.collection("users").document(userId)
        val snapshot = userRef.get().await()
        return snapshot.getLong("balance")?.toDouble() ?: 0.0
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
                val items = querySnapshot.documents.mapNotNull { document ->
                    document.data?.let { data ->
                        val item = Item.fromMap(data)
                        item.uid = document.id // Assign Firestore document ID as uid
                        item
                    }
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

    // Get Item Quantity from Firestore
    suspend fun getItemQuantity(itemId: String): Int {
        val itemRef = db.collection("items").document(itemId)
        val snapshot = itemRef.get().await()
        return snapshot.getLong("quantity")?.toInt() ?: 0
    }

    // Update Item Quantity in Firestore (subtracting purchased amount)
    suspend fun subtractItemQuantity(itemId: String, quantityToSubtract: Int) {
        val itemRef = db.collection("items").document(itemId)
        val snapshot = itemRef.get().await()
        val currentQuantity = snapshot.getLong("quantity")?.toInt() ?: 0

        // Ensure the new quantity is non-negative
        val newQuantity = (currentQuantity - quantityToSubtract).coerceAtLeast(0)

        // Update the quantity in Firestore
        itemRef.update("quantity", newQuantity).await()
    }

    // Generic method to fetch a single document
    fun fetchDocument(
        collectionPath: String,
        documentId: String,
        onSuccess: (Map<String, Any>?) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection(collectionPath).document(documentId).get()
            .addOnSuccessListener { documentSnapshot ->
                onSuccess(documentSnapshot.data)
            }
            .addOnFailureListener { onFailure(it) }
    }

    // Generic method to update fields in a document
    fun updateDocumentFields(
        collectionPath: String,
        documentId: String,
        updates: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection(collectionPath).document(documentId).update(updates)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}
