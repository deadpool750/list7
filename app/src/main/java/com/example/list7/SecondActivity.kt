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

class SecondActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        //firebaseAuth instance
        auth = FirebaseAuth.getInstance()

        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val passwordConfirmEditText = findViewById<EditText>(R.id.passwordconfirmEditText)
        val ageSwitch = findViewById<Switch>(R.id.switch1)
        val signUpButton = findViewById<Button>(R.id.loginButton)
        val loginTextView = findViewById<TextView>(R.id.clickableregisterTextView)

        ageSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                ageSwitch.text = "YES"
            } else {
                ageSwitch.text = "NO"
            }
        }

        //listener for the Sign-Up button
        signUpButton.setOnClickListener {
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = passwordConfirmEditText.text.toString().trim()
            val isAbove18 = ageSwitch.isChecked

            //ssdsds
            //validating inputs
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


            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        //registration successful
                        showToast("Registration successful!")
                        //navigate to another activity or log the user in
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        //registration failed
                        showToast("Registration failed: ${task.exception?.message}")
                    }
                }
        }

        //navigating to login if log in textview is clicked
        loginTextView.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
