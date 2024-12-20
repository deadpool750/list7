package com.example.list7

import android.os.Bundle
import android.app.DatePickerDialog
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.firebaseauthdemo.firebase.FirestoreClass
import com.example.list7.firebase.User
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import java.util.Calendar

class CompleteProfileActivity : AppCompatActivity() {
    private lateinit var nameInput: EditText
    private lateinit var surnameInput: EditText
    private lateinit var emailInput: EditText
    private lateinit var phoneInput: EditText
    private lateinit var addressInput: EditText
    private lateinit var finishButton: Button
    private lateinit var dobButton: Button
    private lateinit var dobText: EditText
    private lateinit var ageText: EditText

    private var selectedDateOfBirth: String = ""

    private val auth = FirebaseAuth.getInstance()
    private val firestoreClass = FirestoreClass()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_complete_profile)
        enableEdgeToEdge()

        // Initialize views
        nameInput = findViewById(R.id.nameInput)
        surnameInput = findViewById(R.id.surnameInput)
        emailInput = findViewById(R.id.emailInput)
        phoneInput = findViewById(R.id.phoneInput)
        addressInput = findViewById(R.id.addressInput)
        finishButton = findViewById(R.id.finishButton)
        dobButton = findViewById(R.id.dobButton)
        dobText = findViewById(R.id.dobText)
        ageText = findViewById(R.id.ageText)

        val userId = auth.currentUser?.uid

        if (userId != null) {
            // Load user data from Firestore
            lifecycleScope.launch {
                try {
                    val userData = firestoreClass.loadUserData(userId)
                    if (userData != null) {
                        val user = User.fromMap(userData)
                        nameInput.setText(user.name)
                        surnameInput.setText(user.surname)
                        emailInput.setText(user.email)
                        phoneInput.setText(user.phoneNumber)
                        addressInput.setText(user.address)
                    } else {
                        Toast.makeText(this@CompleteProfileActivity, "User data not found", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    Toast.makeText(this@CompleteProfileActivity, "Error loading user data: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this@CompleteProfileActivity, "User not logged in", Toast.LENGTH_SHORT).show()
        }
        dobButton.setOnClickListener { openDatePicker() }

        finishButton.setOnClickListener {
            if (userId != null) {
                lifecycleScope.launch {
                    updateUserData(userId)
                }
            } else {
                Toast.makeText(this@CompleteProfileActivity, "User not logged in", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun updateUserData(userId: String) {
        val name = nameInput.text.toString()
        val surname = surnameInput.text.toString()
        val email = emailInput.text.toString()
        val phone = phoneInput.text.toString()
        val address = addressInput.text.toString()

        if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || phone.isEmpty() || address.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedData = mapOf(
            "name" to name,
            "surname" to surname,
            "email" to email,
            "phoneNumber" to phone,
            "address" to address
        )

        try {
            firestoreClass.updateUserData(userId, updatedData)
            Toast.makeText(this@CompleteProfileActivity, "User data updated successfully", Toast.LENGTH_SHORT).show()
            setResult(RESULT_OK)
            finish()
        } catch (e: Exception) {
            Toast.makeText(this@CompleteProfileActivity, "Error updating user data: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    private fun openDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                selectedDateOfBirth = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                dobText.setText(selectedDateOfBirth)

                // Calculate and display age
                val age = calculateAge(selectedDateOfBirth)
                ageText.setText("Age: $age")
            },
            year,
            month,
            day
        )

        // Restrict selection to past dates only
        datePickerDialog.datePicker.maxDate = calendar.timeInMillis

        datePickerDialog.show()
    }
    private fun calculateAge(dateOfBirth: String): Int {
        val parts = dateOfBirth.split("-")
        if (parts.size != 3) return 0

        val birthYear = parts[0].toInt()
        val birthMonth = parts[1].toInt()
        val birthDay = parts[2].toInt()

        val today = Calendar.getInstance()
        var age = today.get(Calendar.YEAR) - birthYear

        // Adjust age if the current date is before the birthday
        if (today.get(Calendar.MONTH) < birthMonth - 1 ||
            (today.get(Calendar.MONTH) == birthMonth - 1 && today.get(Calendar.DAY_OF_MONTH) < birthDay)
        ) {
            age--
        }

        return age
    }
}
