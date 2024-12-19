package com.example.list7

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.firebaseauthdemo.firebase.FirestoreClass
import com.example.list7.firebase.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class HomeActivity : AppCompatActivity() {
    private lateinit var nameInput: EditText
    private lateinit var surnameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var addressInput: EditText
    private lateinit var finishButton: Button

    // Firebase authentication and Firestore class instances
    private val auth = FirebaseAuth.getInstance()
    private val firestoreClass = FirestoreClass()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        enableEdgeToEdge()

        // Initialize views
        nameInput = findViewById(R.id.nameInput)
        surnameInput = findViewById(R.id.surnameInput)
        emailInput = findViewById(R.id.emailInput)
        phoneInput = findViewById(R.id.phoneInput)
        addressInput = findViewById(R.id.addressInput)
        finishButton = findViewById(R.id.finishButton)

        val userId = auth.currentUser?.uid

        if (userId != null) {
            // Load user data from Firestore
            lifecycleScope.launch {
                try {
                    val userData = firestoreClass.loadUserData(userId)
                    if (userData != null) {
                        // Populate
                        val user = User.fromMap(userData)
                        nameInput.setText(user.name)
                        surnameInput.setText(user.surname)
                        emailInput.setText(user.email)
                        phoneInput.setText(user.phoneNumber)
                        addressInput.setText(user.address.toString())
                    } else {
                        Toast.makeText(this@HomeActivity, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@HomeActivity, "Error loading user data: ${e.message}", Toast.LENGTH_SHORT).show()

                }
            }
        }
        finishButton.setOnClickListener {
            if (userId != null) {
                lifecycleScope.launch {
                    updateUserData(userId) // Save data asynchronously
                }
            } else {
                Toast.makeText(this@HomeActivity, "User not logged in", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateUserData(userId: String) {
        val updatedData = mapOf(
            "name" to nameInput.text.toString(),
            "surname" to surnameInput.text.toString(),
            "email" to emailInput.text.toString(),
            "phoneNumber" to phoneInput.text.toString(),
            "address" to addressInput.text.toString()
        )
        try {
            lifecycleScope.launch {
                firestoreClass.updateUserData(userId, updatedData)
                Toast.makeText(this@HomeActivity, "User data updated successfully", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
                }
        } catch (e: Exception) {
            Toast.makeText(this@HomeActivity, "Error updating user data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}