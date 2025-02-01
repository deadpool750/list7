package com.example.list7

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

/**
 * SecondActivity handles the user registration process, allowing users to sign up for an account.
 * It validates the input fields, creates a new user using Firebase Authentication, and handles
 * successful or failed registration attempts.
 */
class SecondActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    /**
     * Initializes the activity, sets up the UI elements, and configures listeners for user input.
     * It also handles user registration and navigates to the login screen upon successful registration.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        // Firebase Authentication instance
        auth = FirebaseAuth.getInstance()

        // Finding UI components by their IDs
        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val passwordConfirmEditText = findViewById<EditText>(R.id.passwordconfirmEditText)
        val ageSwitch = findViewById<Switch>(R.id.switch1)
        val signUpButton = findViewById<Button>(R.id.loginButton)
        val loginTextView = findViewById<TextView>(R.id.clickableregisterTextView)

        // Listener for the age switch, displaying "YES" or "NO"
        ageSwitch.setOnCheckedChangeListener { _, isChecked ->
            ageSwitch.text = if (isChecked) "YES" else "NO"
        }

        // Listener for the Sign-Up button
        signUpButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = passwordConfirmEditText.text.toString().trim()
            val isAbove18 = ageSwitch.isChecked

            // Validate input fields
            if (name.isEmpty()) {
                showToast("Please enter your name")
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                showToast("Please enter your email")
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                showToast("Please enter your password")
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                showToast("Passwords do not match")
                return@setOnClickListener
            }

            if (!isAbove18) {
                showToast("You must be over 18 years old to register")
                return@setOnClickListener
            }

            // Create a new user using Firebase Authentication
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Registration successful
                        showToast("Registration successful!")
                        // Navigate to the login screen
                        startActivity(Intent(this, MainActivity::class.java))
                        finish() // Close the registration activity
                    } else {
                        // Registration failed
                        showToast("Registration failed: ${task.exception?.message}")
                    }
                }
        }

        // Listener to navigate to the login screen if "Log In" text is clicked
        loginTextView.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Close the registration activity
        }
    }

    /**
     * Displays a short toast message with the given text.
     *
     * @param message The message to display in the toast.
     */
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
