package com.example.list7

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
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

    }
}
