package com.example.vrijeme

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

class MainActivity : ComponentActivity() {
    private lateinit var locationProviderClient: FusedLocationProviderClient
    private var permissionsGranted: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        checkAndRequestPermissions()
    }

    private fun checkAndRequestPermissions() {
        if (checkPermission()) {
            permissionsGranted = true
            getCurrentLocation()
        } else {
            requestPermissions()
        }
    }

    private fun getCurrentLocation() {
        if (isLocationEnabled()) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions()
                return
            }
            locationProviderClient.lastLocation.addOnSuccessListener(this) { location ->
                if (location == null) {
                    Toast.makeText(this, "Failed to get location. Make sure location is enabled on the device.", Toast.LENGTH_SHORT).show()
                } else {
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses: List<Address>? = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    val cityName = addresses?.getOrNull(0)?.locality
                    if (cityName != null) {
                        Toast.makeText(this, "Location acquired: $cityName", Toast.LENGTH_SHORT).show()
                        navigateToWeatherActivity(location.latitude, location.longitude, cityName)
                    } else {
                        Toast.makeText(this, "City name not found", Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener(this) { exception ->
                Toast.makeText(this, "Failed to get location: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Location services are turned off. Turn on location services.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    private fun navigateToWeatherActivity(latitude: Double, longitude: Double, cityName: String) {
        val weatherIntent = Intent(this, WeatherActivity::class.java).apply {
            putExtra("latitude", latitude)
            putExtra("longitude", longitude)
            putExtra("cityName", cityName)
        }
        startActivity(weatherIntent)
        finish()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_ACCESS_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(applicationContext, "Permission granted", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            } else{
                Toast.makeText(applicationContext, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
    }
}