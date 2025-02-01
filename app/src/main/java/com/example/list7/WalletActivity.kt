package com.example.list7

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

/**
 * WalletActivity allows the user to add balance to their wallet using a valid credit card.
 * It validates card details, ensures the entered amount is valid, and provides notifications
 * to confirm the transaction. The activity also handles the request for notification permissions
 * on Android versions where it is required (Android 13+).
 */
class WalletActivity : AppCompatActivity() {

    private lateinit var cardNumberInput: EditText
    private lateinit var expiryDateInput: EditText
    private lateinit var cvvInput: EditText
    private lateinit var balanceInput: EditText
    private lateinit var addBalanceButton: Button

    // Predefined set of valid cards
    private val validCards = listOf(
        Triple("4111111111111111", "12/25", "123"), // Card 1: Number, Expiry, CVV
        Triple("5500000000000004", "01/26", "456"), // Card 2
        Triple("340000000000009", "03/27", "789")   // Card 3
    )

    /**
     * Initializes the activity, sets up the UI components, notification channel,
     * and the click listener for the 'Add Balance' button. It also validates card information
     * and handles adding balance when the button is clicked.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)

        // Set up ActionBar with Up Button
        supportActionBar?.apply {
            title = "Wallet"
            setDisplayHomeAsUpEnabled(true)
        }

        // Initialize fields
        cardNumberInput = findViewById(R.id.cardNumberInput)
        expiryDateInput = findViewById(R.id.expiryDateInput)
        cvvInput = findViewById(R.id.cvvInput)
        balanceInput = findViewById(R.id.balanceInput)
        addBalanceButton = findViewById(R.id.addBalanceButton)

        // Set up notification channel (for Android 8+)
        createNotificationChannel()

        // Add balance button click listener
        addBalanceButton.setOnClickListener {
            val cardNumber = cardNumberInput.text.toString()
            val expiryDate = expiryDateInput.text.toString()
            val cvv = cvvInput.text.toString()
            val amount = balanceInput.text.toString().toIntOrNull()

            if (validateCard(cardNumber, expiryDate, cvv)) {
                if (amount != null && amount > 0) {
                    Toast.makeText(this, "Balance added: $amount", Toast.LENGTH_SHORT).show()
                    requestNotificationPermission {
                        showNotification(amount)
                    }
                    balanceInput.text.clear()
                } else {
                    Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Invalid credit card details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Validates the entered card details against a predefined list of valid cards.
     * @param cardNumber The card number entered by the user.
     * @param expiryDate The expiry date entered by the user.
     * @param cvv The CVV entered by the user.
     * @return True if the card details are valid, otherwise false.
     */
    private fun validateCard(cardNumber: String, expiryDate: String, cvv: String): Boolean {
        return validCards.any { it.first == cardNumber && it.second == expiryDate && it.third == cvv }
    }

    /**
     * Creates a notification channel for Android 8 and above. This channel is used to display
     * wallet transaction notifications.
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "wallet_notifications",
                "Wallet Notifications",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notifications for wallet transactions"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    /**
     * Displays a notification confirming the amount added to the wallet.
     * @param amount The amount added to the wallet.
     */
    private fun showNotification(amount: Int) {
        val builder = NotificationCompat.Builder(this, "wallet_notifications")
            .setSmallIcon(R.drawable.ic_wallet) // Ensure you have an icon named ic_wallet in res/drawable
            .setContentTitle("Wallet Update")
            .setContentText("Successfully added $amount to your wallet.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(this)) {
            notify(1, builder.build())
        }
    }

    /**
     * Requests notification permission if needed (Android 13+).
     * If permission is granted, it triggers the provided callback.
     * @param onPermissionGranted Callback to be invoked if permission is granted.
     */
    private fun requestNotificationPermission(onPermissionGranted: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Permission already granted
                onPermissionGranted()
            } else {
                // Request the POST_NOTIFICATIONS permission
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
        } else {
            // Permission not required for earlier Android versions
            onPermissionGranted()
        }
    }

    /**
     * Handles the result of the notification permission request.
     * Displays a toast indicating whether the permission was granted or denied.
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted
            Toast.makeText(this, "Notification permission granted", Toast.LENGTH_SHORT).show()
        } else {
            // Permission denied
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Handles the click of the home/up button in the ActionBar to finish the activity.
     * @param item The menu item clicked.
     * @return True if the item was handled, otherwise false.
     */
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
