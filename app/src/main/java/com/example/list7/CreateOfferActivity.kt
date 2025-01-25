package com.example.list7

import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.SyncStateContract
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import android.Manifest
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts


class CreateOfferActivity : AppCompatActivity() {

    private val REQUEST_CAMERA = 100
    private val REQUEST_GALLERY = 200
    private val PERMISSION_CODE = 300

    private lateinit var imageView: ImageView
    private lateinit var editTextPrice: EditText
    private lateinit var editTextDescription: EditText
    private lateinit var buttonSubmit: Button

    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>
    private lateinit var galleryLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_create_offer)

        val buttonUpload = findViewById<Button>(R.id.uploadButton)
        imageView = findViewById(R.id.imageToUpload)
        editTextPrice = findViewById(R.id.item_price)
        editTextDescription = findViewById(R.id.item_description)
        buttonSubmit = findViewById(R.id.submitOfferButton)

        // Set up the ActivityResultLaunchers
        setupActivityResultLaunchers()

        //Set onClick Listeners
        buttonUpload.setOnClickListener{ showOptionsDialog() }
        buttonSubmit.setOnClickListener{ handleSubmit() }
    }
    private fun setupActivityResultLaunchers() {
        // Camera launcher
        cameraLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val photo = result.data?.extras?.get("data") as Bitmap
                imageView.setImageBitmap(photo)
            }
        }

        // Gallery launcher
        galleryLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val selectedImage: Uri? = result.data?.data
                imageView.setImageURI(selectedImage)
            }
        }
    }
    private fun showOptionsDialog() {
        val options = arrayOf("Camera", "Gallery")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Select Option")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                }
            }
            .show()
    }
    private fun openCamera() {
        if (checkPermissions()) {
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(cameraIntent, REQUEST_CAMERA)
        }
    }
    private fun openGallery() {
        if (checkPermissions()) {
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, REQUEST_GALLERY)
        }
    }
    private fun checkPermissions(): Boolean {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ), PERMISSION_CODE)
            return false
        }
        return true
    }
    private fun handleSubmit() {
        val price = editTextPrice.text.toString().trim()
        val description = editTextDescription.text.toString().trim()

        // Validation
        if (price.isEmpty()) {
            Toast.makeText(this, "Please enter a price", Toast.LENGTH_SHORT).show()
            return
        }
        if (description.isEmpty()) {
            Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show()
            return
        }

        // Display the details in a Toast
        Toast.makeText(this, "Price: $price\nDescription: $description", Toast.LENGTH_LONG).show()
    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissions denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}