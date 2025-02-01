package com.example.list7

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseauthdemo.firebase.FirestoreClass
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ShopActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: ItemAdapter
    private val itemList = mutableListOf<Item>()
    private val firestoreClass = FirestoreClass()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

        // Set up the ActionBar
        supportActionBar?.title = "Shop"

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.homeRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set up the ItemAdapter with the Add to Cart callback
        itemAdapter = ItemAdapter(this, itemList) { item ->
            handleAddToCart(item)
        }
        recyclerView.adapter = itemAdapter

        // Checkout button logic
        findViewById<Button>(R.id.checkoutButton).setOnClickListener {
            handleCheckout()
        }

        findViewById<Button>(R.id.addOfferButton).setOnClickListener {
            // Navigate to CreateOfferActivity
            val intent = Intent(this, CreateOfferActivity::class.java)
            startActivity(intent)
        }

        // Load items from Firestore
        loadItems()
    }

    private fun loadItems() {
        firestoreClass.loadItems(
            collectionPath = "items",
            onSuccess = { items ->
                itemList.clear()
                itemList.addAll(items)
                itemAdapter.notifyDataSetChanged()
            },
            onFailure = { error ->
                Toast.makeText(this, "Failed to load items: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun handleAddToCart(item: Item) {
        if (item.quantity <= 0) {
            // Notify the user if the item is out of stock
            Toast.makeText(this, "${item.itemName} is out of stock", Toast.LENGTH_SHORT).show()
            return
        }

        // Add the item to the local cart
        CartManager.addItemToCart(item)

        // Launch a coroutine to update Firestore and handle UI changes
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Subtract 1 from the item's quantity in Firestore
                firestoreClass.subtractItemQuantity(
                    itemId = item.uid,
                    quantityToSubtract = 1
                )

                // Update local item quantity on the main thread
                withContext(Dispatchers.Main) {
                    item.quantity -= 1
                    itemAdapter.notifyDataSetChanged()

                    // Show "added to cart" toast once
                    Toast.makeText(this@ShopActivity, "${item.itemName} added to cart", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                // Handle Firestore update errors on the main thread
                withContext(Dispatchers.Main) {
                    // Revert the cart addition if Firestore update fails
                    CartManager.removeItem(item)
                    Toast.makeText(this@ShopActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun handleCheckout() {
        if (CartManager.getCartItems().isNotEmpty()) {
            startActivity(Intent(this, CartActivity::class.java))
        } else {
            Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show()
        }
    }



}
