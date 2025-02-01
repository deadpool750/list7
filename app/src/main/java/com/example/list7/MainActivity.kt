package com.example.list7

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

/**
 * MainActivity handles the user login process, including verifying user credentials
 * and managing login state using Firebase Authentication and SharedPreferences.
 */
class MainActivity : AppCompatActivity() {

    // Firebase authentication instance
    private lateinit var auth: FirebaseAuth

    /**
     * Initializes the activity, checks the login state, and sets up view elements and listeners.
     * If the user is already logged in, they are redirected to HomeActivity.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide() // Hides the action bar
        setContentView(R.layout.activity_main)

        // Check if the user is already logged in
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            // Redirect to HomeActivity if already logged in
            startActivity(Intent(this, HomeActivity1::class.java))
            finish()
        }

        // Initialize Firebase authentication
        auth = FirebaseAuth.getInstance()

        // Finding views by their ids in the layout
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerTextView = findViewById<TextView>(R.id.clickableregisterTextView)

        // Listener for login button click
        loginButton.setOnClickListener {
            val email = findViewById<EditText>(R.id.emailEditText).text.toString()
            val password = findViewById<EditText>(R.id.passwordEditText).text.toString()
            loginUser(email, password)
        }

        // Listener for register text view click
        registerTextView.setOnClickListener {
            // Navigate to SecondActivity for user registration
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }
    }

    /**
     * Checks if the provided email is in a valid format using Android's built-in email pattern.
     *
     * @param email The email to validate.
     * @return Boolean indicating whether the email is valid.
     */
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    /**
     * Attempts to log in the user using Firebase Authentication with the provided email and password.
     * Displays a success or failure message based on the result of the login attempt.
     *
     * @param email The email of the user.
     * @param password The password of the user.
     */
    private fun loginUser(email: String, password: String) {

        if (!isValidEmail(email)) {
            // Show a Toast message if the email format is invalid
            Toast.makeText(this, "Invalid Email Format", Toast.LENGTH_SHORT).show()
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                // Firebase `signInWithEmailAndPassword` to attempt user login
                if (task.isSuccessful) {
                    // Save the user's login state in SharedPreferences
                    val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
                    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()

                    // Show a success message
                    Toast.makeText(this, "Login Successful!", Toast.LENGTH_SHORT).show()

                    // Navigate to HomeActivity
                    val intent = Intent(this, HomeActivity1::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish() // Close MainActivity to prevent going back to it
                } else {
                    // Show the error message from Firebase if login fails
                    Toast.makeText(this, "Login Failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                }
            }
    }

    /**
     * Checks if the user is currently logged in by reading the login state from SharedPreferences.
     *
     * @param context The context to access SharedPreferences.
     * @return Boolean indicating whether the user is logged in.
     */
    fun isUserLoggedIn(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("isLoggedIn", true)
    }
}

