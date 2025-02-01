package com.example.firebaseauthdemo.firebase

import com.example.list7.Item
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

/**
 * FirestoreClass provides methods to interact with Firestore for user and item management.
 */
class FirestoreClass {
    private val db = FirebaseFirestore.getInstance()

    /**
     * Adds or updates user data in Firestore.
     * @param userId The ID of the user.
     * @param userData A map containing user data fields.
     */
    suspend fun addUserData(userId: String, userData: Map<String, Any>) {
        db.collection("users").document(userId).set(userData).await()
    }

    /**
     * Updates user data if it exists, otherwise creates a new document.
     * @param userId The ID of the user.
     * @param userData A map containing user data fields.
     */
    suspend fun updateUserData(userId: String, userData: Map<String, Any>) {
        val documentRef = db.collection("users").document(userId)
        val documentSnapshot = documentRef.get().await()

        if (documentSnapshot.exists()) {
            documentRef.update(userData).await()
        } else {
            documentRef.set(userData).await()
        }
    }

    /**
     * Loads user data from Firestore.
     * @param userId The ID of the user.
     * @return A map containing user data if found, otherwise null.
     */
    suspend fun loadUserData(userId: String): Map<String, Any>? {
        return db.collection("users").document(userId).get().await().data
    }

    /**
     * Deletes a user's data from Firestore.
     * @param userId The ID of the user.
     */
    suspend fun deleteUserData(userId: String) {
        db.collection("users").document(userId).delete().await()
    }

    /**
     * Updates the user's balance.
     * @param userId The ID of the user.
     * @param newBalance The updated balance value.
     */
    suspend fun updateUserBalance(userId: String, newBalance: Double) {
        val userRef = db.collection("users").document(userId)
        userRef.update("balance", newBalance).await()
    }

    /**
     * Retrieves the user's balance.
     * @param userId The ID of the user.
     * @return The user's balance as a Double.
     */
    suspend fun getUserBalance(userId: String): Double {
        val userRef = db.collection("users").document(userId)
        val snapshot = userRef.get().await()
        return snapshot.getLong("balance")?.toDouble() ?: 0.0
    }

    /**
     * Adds an item to the Firestore collection.
     * @param collectionPath The path of the Firestore collection.
     * @param item The item to add.
     * @param onSuccess Callback executed on successful addition.
     * @param onFailure Callback executed on failure.
     */
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

    /**
     * Loads items from a Firestore collection.
     * @param collectionPath The path of the Firestore collection.
     * @param onSuccess Callback executed with a list of retrieved items.
     * @param onFailure Callback executed on failure.
     */
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

    /**
     * Deletes items from a Firestore collection.
     * @param collectionPath The path of the Firestore collection.
     * @param itemIds The list of item IDs to delete.
     */
    suspend fun deleteItems(collectionPath: String, itemIds: List<String>) {
        val collectionRef = db.collection(collectionPath)
        for (itemId in itemIds) {
            collectionRef.document(itemId).delete().await()
        }
    }

    /**
     * Retrieves the quantity of an item from Firestore.
     * @param itemId The ID of the item.
     * @return The quantity of the item.
     */
    suspend fun getItemQuantity(itemId: String): Int {
        val itemRef = db.collection("items").document(itemId)
        val snapshot = itemRef.get().await()
        return snapshot.getLong("quantity")?.toInt() ?: 0
    }

    /**
     * Updates the quantity of an item in Firestore by subtracting a specified amount.
     * @param itemId The ID of the item.
     * @param quantityToSubtract The amount to subtract from the current quantity.
     */
    suspend fun subtractItemQuantity(itemId: String, quantityToSubtract: Int) {
        val itemRef = db.collection("items").document(itemId)
        val snapshot = itemRef.get().await()
        val currentQuantity = snapshot.getLong("quantity")?.toInt() ?: 0
        val newQuantity = (currentQuantity - quantityToSubtract).coerceAtLeast(0)
        itemRef.update("quantity", newQuantity).await()
    }

    /**
     * Fetches a single document from Firestore.
     */
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

    /**
     * Updates specific fields in a Firestore document.
     */
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