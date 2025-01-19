package com.example.list7

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomeActivity1 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home1)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Find the buttons by their IDs
        val updateDetailsButton = findViewById<Button>(R.id.updateDetailsButton)
        val goToShopButton = findViewById<Button>(R.id.goToShopButton)
        val goToMapsButton = findViewById<Button>(R.id.goToMapsButton)
        val btnLogout = findViewById<Button>(R.id.btnLogout)


        // Set a click listener on the "Update your Details" button
        updateDetailsButton.setOnClickListener {
            // Navigate to CompleteProfileActivity
            val intent = Intent(this, CompleteProfileActivity::class.java)
            startActivity(intent)
        }

        // Set a click listener on the "Go to shop" button
        goToShopButton.setOnClickListener {
            // Navigate to ShopActivity
            val intent = Intent(this, ShopActivity::class.java)
            startActivity(intent)
        }

        // Set a click listener on the "Go to Maps" button
        goToMapsButton.setOnClickListener {
            // Navigate to MapsActivity
            val intent = Intent(this, MapsActivity::class.java)
            startActivity(intent)
        }

        fun logoutUser() {
            Log.d("Logout", "Clearing session and redirecting to LoginActivity")
            // Clear the login status from SharedPreferences
            val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.clear() // This clears all stored preferences
            editor.apply()

            // Navigate the user back to the login screen
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK // Clear activity stack
            startActivity(intent)

            // Optionally, show a toast message
            Toast.makeText(this, "Logged out successfully", Toast.LENGTH_SHORT).show()
        }

        btnLogout.setOnClickListener {
            // Call the logout function
            logoutUser()
        }
        btnLogout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Logout")
                .setMessage("Are you sure you want to log out?")
                .setPositiveButton("Yes") { _, _ -> logoutUser() }
                .setNegativeButton("Cancel", null)
                .show()
        }

    }
}
