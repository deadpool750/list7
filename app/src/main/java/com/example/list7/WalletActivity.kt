package com.example.list7

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.firebaseauthdemo.firebase.FirestoreClass
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * WalletActivity provides functionality for users to manage their wallet balance.
 * Users can view their current balance, add funds using valid credit card details, and
 * receive notifications for successful transactions.
 */
class WalletActivity : AppCompatActivity() {

    // UI components
    private lateinit var currentBalanceTextView: TextView
    private lateinit var cardNumberInput: EditText
    private lateinit var expiryDateInput: EditText
    private lateinit var cvvInput: EditText
    private lateinit var balanceInput: EditText
    private lateinit var addBalanceButton: Button

    // Firestore class for database operations
    private val firestoreClass = FirestoreClass()

    // Firebase Authentication instance
    private val auth = FirebaseAuth.getInstance()

    // Predefined list of valid cards for demo purposes
    private val validCards = listOf(
        Triple("4111111111111111", "12/25", "123"),
        Triple("5500000000000004", "01/26", "456"),
        Triple("340000000000009", "03/27", "789")
    )

    /**
     * Initializes the activity, sets up UI components, ActionBar, and event listeners.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wallet)

        // Set up ActionBar with back navigation
        supportActionBar?.apply {
            title = "Wallet" // Set the title of the ActionBar
            setDisplayHomeAsUpEnabled(true) // Enable the back button
        }

        // Initialize UI components
        currentBalanceTextView = findViewById(R.id.currentBalanceTextView)
        cardNumberInput = findViewById(R.id.cardNumberInput)
        expiryDateInput = findViewById(R.id.expiryDateInput)
        cvvInput = findViewById(R.id.cvvInput)
        balanceInput = findViewById(R.id.balanceInput)
        addBalanceButton = findViewById(R.id.addBalanceButton)

        // Create notification channel for wallet notifications (required for Android 8+)
        createNotificationChannel()

        // Fetch and display the current wallet balance
        fetchAndDisplayCurrentBalance()

        // Add balance button click listener
        addBalanceButton.setOnClickListener {
            val cardNumber = cardNumberInput.text.toString()
            val expiryDate = expiryDateInput.text.toString()
            val cvv = cvvInput.text.toString()
            val amount = balanceInput.text.toString().toDoubleOrNull()

            // Validate card details and amount
            if (validateCard(cardNumber, expiryDate, cvv)) {
                if (amount != null && amount > 0) {
                    updateBalance(amount) // Update the wallet balance
                } else {
                    Toast.makeText(this, "Enter a valid amount", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Invalid credit card details", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Fetches the current wallet balance from Firestore and displays it on the UI.
     */
    private fun fetchAndDisplayCurrentBalance() {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Coroutine to fetch balance from Firestore
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val currentBalance = firestoreClass.getUserBalance(userId) // Fetch balance
                withContext(Dispatchers.Main) {
                    // Update the TextView with the fetched balance
                    currentBalanceTextView.text = "Current Balance: $${"%.2f".format(currentBalance)}"
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@WalletActivity,
                    "Failed to fetch balance: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * Updates the wallet balance in Firestore and refreshes the displayed balance.
     * @param amount The amount to add to the current balance.
     */
    private fun updateBalance(amount: Double) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        // Coroutine to update balance in Firestore
        CoroutineScope(Dispatchers.Main).launch {
            try {
                val currentBalance = firestoreClass.getUserBalance(userId) // Fetch current balance
                val newBalance = currentBalance + amount // Calculate new balance
                firestoreClass.updateUserBalance(userId, newBalance) // Update balance in Firestore

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@WalletActivity,
                        "Balance updated successfully! New balance: $${"%.2f".format(newBalance)}",
                        Toast.LENGTH_SHORT
                    ).show()
                    fetchAndDisplayCurrentBalance() // Refresh displayed balance
                }
            } catch (e: Exception) {
                Toast.makeText(
                    this@WalletActivity,
                    "Failed to update balance: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    /**
     * Validates the entered credit card details against the predefined list of valid cards.
     * @param cardNumber The card number entered by the user.
     * @param expiryDate The expiry date entered by the user.
     * @param cvv The CVV entered by the user.
     * @return True if the card details are valid, false otherwise.
     */
    private fun validateCard(cardNumber: String, expiryDate: String, cvv: String): Boolean {
        return validCards.any { it.first == cardNumber && it.second == expiryDate && it.third == cvv }
    }

    /**
     * Creates a notification channel for wallet transaction notifications.
     * Required for Android 8.0 (API level 26) and above.
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
     * Handles the ActionBar back button press to finish the activity.
     * @param item The menu item clicked.
     * @return True if the back button press was handled, false otherwise.
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
