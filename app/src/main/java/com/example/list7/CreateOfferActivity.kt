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

/**
 * Activity for creating a new offer. Users can input item details such as name, price, quantity, and UID.
 * Upon successful creation, a system notification is sent, and the offer is saved to Firestore.
 */
class CreateOfferActivity : AppCompatActivity() {

    private val firestoreClass = FirestoreClass() // Firestore instance for database operations
    private val channelId = "offer_notifications" // Notification channel ID
    private val notificationId = 1 // Unique ID for the notification

    /**
     * Called when the activity is created. Initializes the UI and sets up the notification channel.
     *
     * @param savedInstanceState The saved instance state of the activity.
     */
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

    /**
     * Creates a notification channel for system notifications (required for Android 8.0+).
     */
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

    /**
     * Sends a system notification to inform the user that an offer has been created.
     *
     * @param itemName The name of the item for which the offer was created.
     */
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

    /**
     * Handles the Back Button in the ActionBar to navigate back to the previous activity.
     *
     * @return True to indicate the event was handled.
     */
    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}