package com.example.list7

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView

class ShopActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_shop)

        // Handle window insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Find the button by its ID
        val updateDetailsButton = findViewById<Button>(R.id.updateDetailsButton)

        // Set a click listener on the button
        updateDetailsButton.setOnClickListener {
            // Navigate to CompleteProfileActivity
            val intent = Intent(this, CompleteProfileActivity::class.java)
            startActivity(intent)
        }
        val myDataset = DataSource().loadEquipment()

        val recyclerView = findViewById<RecyclerView>(R.id.homeRecyclerView)
        recyclerView.adapter = ItemAdapter(this, myDataset)

        recyclerView.setHasFixedSize(true)
    }
}