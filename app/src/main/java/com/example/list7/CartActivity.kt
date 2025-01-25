package com.example.list7

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseauthdemo.firebase.FirestoreClass
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CartActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var buyButton: Button
    private val firestoreClass = FirestoreClass()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // Set up ActionBar with Up Button
        supportActionBar?.apply {
            title = "Your Cart"
            setDisplayHomeAsUpEnabled(true)
        }

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.cartRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set up the CartAdapter
        cartAdapter = CartAdapter(this, CartManager.getCartItems())
        recyclerView.adapter = cartAdapter

        // Buy Button
        buyButton = findViewById(R.id.buyButton)
        buyButton.setOnClickListener {
            handlePurchase()
        }
    }

    private fun handlePurchase() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Calculate the total cost of items in the cart
        val totalCost = CartManager.getCartItems().sumOf { it.price }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Get user's current balance
                val currentBalance = firestoreClass.getUserBalance(userId)

                if (currentBalance >= totalCost) {
                    // Subtract total cost from the user's balance
                    val newBalance = currentBalance - totalCost
                    firestoreClass.updateUserBalance(userId, newBalance)

                    // Update item quantities in Firestore
                    updateItemQuantityInFirestore()

                    Toast.makeText(
                        this@CartActivity,
                        "Purchase successful! New balance: $$newBalance",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Clear cart locally
                    CartManager.clearCart()
                    cartAdapter.notifyDataSetChanged()
                } else {
                    Toast.makeText(
                        this@CartActivity,
                        "Insufficient balance. Please add funds.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@CartActivity,
                    "Error during purchase: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private suspend fun updateItemQuantityInFirestore() {
        val cartItems = CartManager.getCartItems()
        val db = FirebaseFirestore.getInstance()
        val itemsCollection = db.collection("items")

        for (item in cartItems) {
            try {
                val itemRef = itemsCollection.document(item.uid) // Use item's unique ID

                // Fetch the current quantity
                val snapshot = itemRef.get().await()
                val currentQuantity = snapshot.getLong("quantity")?.toInt() ?: 0

                // Update the quantity if greater than 0
                if (currentQuantity > 0) {
                    val newQuantity = currentQuantity - 1
                    itemRef.update("quantity", newQuantity).await()

                    if (newQuantity <= 0) {
                        Toast.makeText(this, "Item '${item.itemName}' is out of stock.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Item '${item.itemName}' is no longer available.", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Failed to update item: ${item.itemName}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
