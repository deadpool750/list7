package com.example.list7

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseauthdemo.firebase.FirestoreClass
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * CartActivity handles the user's shopping cart, including displaying the items, updating quantities,
 * calculating the total value, and processing the purchase.
 */
class CartActivity : AppCompatActivity() {

    // Declare views and Firebase objects
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var buyButton: Button
    private lateinit var totalAmountTextView: TextView
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

        // Initialize Views
        recyclerView = findViewById(R.id.cartRecyclerView)
        totalAmountTextView = findViewById(R.id.totalAmountTextView)
        buyButton = findViewById(R.id.buyButton)

        // Set up RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set up CartAdapter with callbacks
        cartAdapter = CartAdapter(
            context = this,
            cartItems = CartManager.getCartItems(),
            onDeleteClick = { item ->
                deleteItemFromCart(item)
                updateTotalCartValue() // Update total value after deletion
            },
            onQuantityChange = { item, newQuantity ->
                updateItemQuantityInCart(item, newQuantity)
                updateTotalCartValue() // Update total value after quantity change
            }
        )
        recyclerView.adapter = cartAdapter

        // Update the total value on startup
        updateTotalCartValue()

        // Buy Button
        buyButton.setOnClickListener { handlePurchase() }
    }

    /**
     * Updates the total value of the cart and displays it in the TextView.
     */
    private fun updateTotalCartValue() {
        // Calculate the total value
        val totalValue = CartManager.getCartItems().sumOf { it.price * it.quantity }
        // Update the TextView
        totalAmountTextView.text = "Total: $${"%.2f".format(totalValue)}"
    }

    /**
     * Handles the purchase process, including checking user balance, validating item quantities,
     * and updating Firestore records. Displays Toast messages for success or errors during purchase.
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
            Toast.makeText(this, "Please select a valid quantity for all items.", Toast.LENGTH_SHORT).show()
            return
        }

        // Calculate the total cost
        val totalCost = cartItems.sumOf { it.price * it.quantity }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val currentBalance = firestoreClass.getUserBalance(userId)

                if (currentBalance >= totalCost) {
                    val newBalance = currentBalance - totalCost
                    firestoreClass.updateUserBalance(userId, newBalance)

                    updateFirestoreItemQuantities()

                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@CartActivity,
                            "Purchase successful! New balance: $$newBalance",
                            Toast.LENGTH_LONG
                        ).show()
                        CartManager.clearCart()
                        cartAdapter.notifyDataSetChanged()
                        updateTotalCartValue() // Clear total value after purchase
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
     * Removes an item from the cart, updates the CartManager and RecyclerView, and shows a Toast message.
     *
     * @param item The item to be removed from the cart.
     */
    private fun deleteItemFromCart(item: Item) {
        CartManager.removeItem(item)
        cartAdapter.updateCartItems(CartManager.getCartItems().toMutableList())
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
            deleteItemFromCart(item)
        } else {
            item.quantity = newQuantity
            cartAdapter.notifyDataSetChanged()
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
