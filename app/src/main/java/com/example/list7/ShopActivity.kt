package com.example.list7

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.firebaseauthdemo.firebase.FirestoreClass

class ShopActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var itemAdapter: ItemAdapter
    private val itemList = mutableListOf<Item>() // List of items
    private val firestoreClass = FirestoreClass() // Firestore class instance

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shop)

        // Set up the ActionBar
        supportActionBar?.apply {
            title = "Shop"
        }

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.homeRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set up the ItemAdapter with Add to Cart callback
        itemAdapter = ItemAdapter(this, itemList) { item ->
            CartManager.addItemToCart(item) // Add the selected item to the cart
            Toast.makeText(this, "${item.itemName} added to cart", Toast.LENGTH_SHORT).show()
        }
        recyclerView.adapter = itemAdapter

        // Checkout button logic
        val checkoutButton = findViewById<Button>(R.id.checkoutButton)
        checkoutButton.setOnClickListener {
            if (CartManager.getCartItems().isNotEmpty()) {
                // Navigate to the CartActivity
                startActivity(Intent(this, CartActivity::class.java))
            } else {
                Toast.makeText(this, "Your cart is empty", Toast.LENGTH_SHORT).show()
            }
        }

        // Load items from Firestore
        loadItemsFromFirestore()
    }

    private fun loadItemsFromFirestore() {
        // Use FirestoreClass to fetch items
        firestoreClass.loadItems(
            collectionPath = "items", // Your Firestore collection path
            onSuccess = { items ->
                itemList.clear() // Clear the list before adding new items
                itemList.addAll(items) // Add items from Firestore
                itemAdapter.notifyDataSetChanged() // Notify adapter about data change
            },
            onFailure = { error ->
                Toast.makeText(this, "Failed to load items: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        )
    }
}
