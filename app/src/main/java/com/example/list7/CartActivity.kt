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

/**
 * CartActivity handles the user's shopping cart, including displaying the items, updating quantities, and processing the purchase.
 * It also integrates with Firebase to manage user balance and item quantities in Firestore.
 */
class CartActivity : AppCompatActivity() {

    // Declare views and Firebase objects
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var buyButton: Button
    private val firestoreClass = FirestoreClass()
    private val auth = FirebaseAuth.getInstance()

    /**
     * Initializes the activity by setting up the RecyclerView, CartAdapter, and Buy button.
     * Also sets the ActionBar title and home button.
     */
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

    /**
     * Handles the purchase process, including checking user balance, validating item quantities,
     * and updating Firestore records.
     * Displays Toast messages for success or errors during purchase.
     */
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

    /**
     * Updates the item quantities in Firestore after a successful purchase.
     * Subtracts the quantity of each item in the cart from the Firestore database.
     */
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

    /**
     * Removes an item from the cart, updates the CartManager and RecyclerView, and shows a Toast message.
     *
     * @param item The item to be removed from the cart.
     */
    private fun deleteItemFromCart(item: Item) {
        // Remove the item from CartManager
        CartManager.removeItem(item)

        // Update the adapter to reflect the changes
        cartAdapter.updateCartItems(CartManager.getCartItems().toMutableList())

        // Show a Toast message
        Toast.makeText(this, "Item '${item.itemName}' removed from cart", Toast.LENGTH_SHORT).show()
    }

    /**
     * Updates the quantity of an item in the cart. If the quantity is zero or less, the item is removed.
     *
     * @param item The item whose quantity is being updated.
     * @param newQuantity The new quantity for the item.
     */
    private fun updateItemQuantityInCart(item: Item, newQuantity: Int) {
        if (newQuantity <= 0) {
            deleteItemFromCart(item) // Remove item if quantity is zero or less
        } else {
            item.quantity = newQuantity
            cartAdapter.notifyDataSetChanged()
        }
    }

    /**
     * Handles item selection from the ActionBar (e.g., the "home" button press).
     *
     * @param item The selected menu item.
     * @return Boolean indicating whether the item was handled.
     */
    override fun onOptionsItemSelected(item: android.view.MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish() // Finish the activity on pressing the home button
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
