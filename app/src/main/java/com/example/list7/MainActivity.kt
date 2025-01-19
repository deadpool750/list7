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

class MainActivity : AppCompatActivity() {
    //auth declaration for firebase
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Check if the user is already logged in
        val sharedPreferences = getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            // Redirect to HomeActivity if already logged in
            startActivity(Intent(this, HomeActivity1::class.java))
            finish()
        }

        //firebase authentication instance.
        auth = FirebaseAuth.getInstance()


        //finding views by their ids in the .xml file
        val loginButton = findViewById<Button>(R.id.loginButton)
        val registerTextView = findViewById<TextView>(R.id.clickableregisterTextView)

        loginButton.setOnClickListener {
            //listener for the login button click.
            val email = findViewById<EditText>(R.id.emailEditText).text.toString()
            val password = findViewById<EditText>(R.id.passwordEditText).text.toString()
            loginUser(email, password)
        }

        registerTextView.setOnClickListener {
            //if the register TextView is clicked, navigate to SecondActivity which is register.
            val intent = Intent(this, SecondActivity::class.java)
            //intent to navigate to SecondActivity.

            startActivity(intent)
        }


    }

    private fun isValidEmail(email: String): Boolean {
        //checking if the email is valid
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun loginUser(email: String, password: String) {

        if (!isValidEmail(email)) {
            //check if email format is valid
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
    fun isUserLoggedIn(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE)
        return sharedPreferences.getBoolean("isLoggedIn", true)
    }
}
