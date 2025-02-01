package com.example.list7

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import com.example.firebaseauthdemo.firebase.FirestoreClass
import com.example.list7.Item

class CreateOfferActivity : AppCompatActivity() {

    private val firestoreClass = FirestoreClass()
    private val channelId = "offer_notifications"
    private val notificationId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_offer)

        // Set up ActionBar
        supportActionBar?.apply {
            title = "Create an Offer" // Rename ActionBar
            setDisplayHomeAsUpEnabled(true) // Add back button
        }

        // Initialize Notification Channel
        createNotificationChannel()

        // Initialize views
        val itemNameEditText = findViewById<EditText>(R.id.offerNameEditText)
        val itemPriceEditText = findViewById<EditText>(R.id.offerPriceEditText)
        val itemQuantityEditText = findViewById<EditText>(R.id.offerQuantityEditText)
        val itemUidEditText = findViewById<EditText>(R.id.offerUidEditText)
        val createOfferButton = findViewById<Button>(R.id.createOfferButton)

        // Handle the "Create Offer" button click
        createOfferButton.setOnClickListener {
            val itemName = itemNameEditText.text.toString().trim()
            val itemPrice = itemPriceEditText.text.toString().trim()
            val itemQuantity = itemQuantityEditText.text.toString().trim()
            val itemUid = itemUidEditText.text.toString().trim()

            // Validate input
            if (itemName.isEmpty() || itemPrice.isEmpty() || itemQuantity.isEmpty() || itemUid.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Convert price to Double
            val price = itemPrice.toDoubleOrNull()
            if (price == null) {
                Toast.makeText(this, "Please enter a valid price", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create an item object
            val item = Item(
                itemName = itemName,
                price = price, // Use Double for price
                quantity = itemQuantity.toIntOrNull() ?: 0,
                uid = itemUid
            )

            // Add item to Firestore
            firestoreClass.addItem(
                collectionPath = "items",
                item = item,
                onSuccess = {
                    // Show toast and system notification
                    Toast.makeText(this, "Offer created successfully!", Toast.LENGTH_SHORT).show()
                    sendSystemNotification(itemName)
                    finish() // Go back to the previous activity
                },
                onFailure = { error ->
                    Toast.makeText(this, "Failed to create offer: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            )
        }
    }

    // Create Notification Channel (Required for Android 8.0+)
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Offer Notifications",
                NotificationManager.IMPORTANCE_HIGH // High importance for system notifications
            ).apply {
                description = "Notifications for created offers"
            }
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    // Send System Notification
    private fun sendSystemNotification(itemName: String) {
        val notificationManager = getSystemService(NotificationManager::class.java)

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification_tick) // Use the new tick icon
            .setContentTitle("Offer Created")
            .setContentText("The offer for \"$itemName\" was successfully created.")
            .setPriority(NotificationCompat.PRIORITY_HIGH) // High priority for visibility
            .setAutoCancel(true) // Remove notification when clicked
            .build()

        // Notify the system
        notificationManager.notify(notificationId, notification)
    }

    // Handle Back Button in ActionBar
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}
