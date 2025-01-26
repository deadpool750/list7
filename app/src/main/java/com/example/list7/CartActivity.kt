package com.example.list7

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseauthdemo.firebase.FirestoreClass
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CartActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var buyButton: Button
    private val firestoreClass = FirestoreClass()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // Set up ActionBar
        supportActionBar?.apply {
            title = "Your Cart"
            setDisplayHomeAsUpEnabled(true)
        }

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.cartRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set up the CartAdapter with delete and quantity change callbacks
        cartAdapter = CartAdapter(
            context = this,
            cartItems = CartManager.getCartItems(),
            onDeleteClick = { item -> deleteItemFromCart(item) },
            onQuantityChange = { item, newQuantity -> updateItemQuantityInCart(item, newQuantity) }
        )
        recyclerView.adapter = cartAdapter

        // Buy Button
        buyButton = findViewById(R.id.buyButton)
        buyButton.setOnClickListener { handlePurchase() }
    }

    private fun handlePurchase() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if all items have valid quantities
        val cartItems = CartManager.getCartItems()
        val invalidItems = cartItems.filter { it.quantity <= 0 }
        if (invalidItems.isNotEmpty()) {
            // Display message for invalid items
            Toast.makeText(this, "Please select a valid quantity for all items.", Toast.LENGTH_SHORT).show()
            return
        }

        // Calculate the total cost of items in the cart
        val totalCost = cartItems.sumOf { it.price * it.quantity }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                // Get user's current balance
                val currentBalance = firestoreClass.getUserBalance(userId)

                if (currentBalance >= totalCost) {
                    // Subtract total cost from the user's balance
                    val newBalance = currentBalance - totalCost
                    firestoreClass.updateUserBalance(userId, newBalance)

                    // Update item quantities in Firestore
                    updateFirestoreItemQuantities()

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@CartActivity,
                            "Purchase successful! New balance: $$newBalance",
                            Toast.LENGTH_LONG
                        ).show()
                        // Clear cart locally
                        CartManager.clearCart()
                        cartAdapter.notifyDataSetChanged()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@CartActivity,
                            "Insufficient balance. Please add funds.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@CartActivity,
                        "Error during purchase: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private suspend fun updateFirestoreItemQuantities() {
        val cartItems = CartManager.getCartItems()
        for (item in cartItems) {
            try {
                firestoreClass.subtractItemQuantity(
                    itemId = item.uid,
                    quantityToSubtract = item.quantity
                )
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@CartActivity,
                        "Error updating item '${item.itemName}': ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun deleteItemFromCart(item: Item) {
        // Remove the item from CartManager
        CartManager.removeItem(item)

        // Update the adapter to reflect the changes
        cartAdapter.updateCartItems(CartManager.getCartItems().toMutableList())

        // Show a Toast message
        Toast.makeText(this, "Item '${item.itemName}' removed from cart", Toast.LENGTH_SHORT).show()
    }


    private fun updateItemQuantityInCart(item: Item, newQuantity: Int) {
        if (newQuantity <= 0) {
            deleteItemFromCart(item) // Remove item if quantity is zero or less
        } else {
            item.quantity = newQuantity
            cartAdapter.notifyDataSetChanged()
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
