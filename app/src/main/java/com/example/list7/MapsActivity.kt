package com.example.list7

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.list7.databinding.ActivityMapsBinding
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.net.PlacesClient

/**
 * MapsActivity handles the display of a Google Map with custom markers, allowing the user
 * to view and search for stores by name. It uses the Google Maps API and Places API.
 */
class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var placesClient: PlacesClient
    private val defaultLocation = LatLng(51.1079, 17.0595) // A1 Building, Politechnika Wrocławska
    private val defaultZoom = 15f

    // Define coordinates for the imaginary stores
    private val storeLocations = mapOf(
        "Store 1" to LatLng(51.1085, 17.0605),
        "Store 2" to LatLng(51.1090, 17.0610),
        "Store 3" to LatLng(51.1070, 17.0620),
        "Store 4" to LatLng(51.1065, 17.0580)
    )

    /**
     * Initializes the activity, sets up the map, and configures the search bar.
     * Also initializes the Places API and prepares the map for use.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up the action bar for the back button
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Initialize the Places API
        Places.initialize(applicationContext, getString(R.string.google_maps_key))
        placesClient = Places.createClient(this)

        // Obtain the SupportMapFragment and set up the map
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // Set up the search bar
        setupSearchBar()
    }

    /**
     * Called when the map is ready to be used. It configures the map's settings,
     * adds markers for the default location and stores, and enables location tracking.
     *
     * @param googleMap The GoogleMap object to be configured.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // Enable zoom controls on the map
        mMap.uiSettings.isZoomControlsEnabled = true

        // Enable the user's location
        enableUserLocation()

        // Move the camera to the default location
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, defaultZoom))

        // Add a marker at the default location (A1 Building)
        mMap.addMarker(MarkerOptions().position(defaultLocation).title("Trekking Gurus.zoo headquarters"))

        // Add markers for the imaginary stores
        addStoreMarkers()
    }

    /**
     * Enables the user's location on the map, if the appropriate permissions are granted.
     * If permissions are not granted, the map will use the default location.
     */
    private fun enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Do not request location permissions, just use the default location
            return
        }
        mMap.isMyLocationEnabled = true
    }

    /**
     * Sets up the search bar to allow users to search for store locations by name.
     * When a search query is submitted, it attempts to move the map to the specified location.
     */
    private fun setupSearchBar() {
        val searchView = binding.searchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (!query.isNullOrEmpty()) {
                    searchLocation(query)
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    /**
     * Searches for a store by its name and moves the map to that location.
     * If the store is found, it adds a marker for the store and zooms the camera.
     * If the store is not found, it displays a toast message.
     *
     * @param locationName The name of the store to search for.
     */
    private fun searchLocation(locationName: String) {
        val location = storeLocations[locationName]

        if (location != null) {
            mMap.clear() // Clear existing markers
            mMap.addMarker(
                MarkerOptions()
                    .position(location)
                    .title(locationName)
            )
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, defaultZoom))
        } else {
            Toast.makeText(this, "Store not found", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Adds markers for the predefined store locations on the map.
     */
    private fun addStoreMarkers() {
        // Add markers for the 4 imaginary stores
        for ((storeName, storeLocation) in storeLocations) {
            mMap.addMarker(
                MarkerOptions()
                    .position(storeLocation)
                    .title(storeName)
            )
        }
    }

    /**
     * Handles item selections in the options menu. Specifically, it handles the back button
     * action to finish the current activity and return to the previous one.
     *
     * @param item The menu item that was selected.
     * @return True if the item was handled, otherwise false.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish() // Navigate back to HomeActivity1
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
