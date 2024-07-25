package com.example.vrijeme.helpers

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.provider.Settings
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.example.vrijeme.WeatherActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.util.Locale

class LocationHelper(private val activity: Activity) {

    private val locationProviderClient: FusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity)

    fun checkAndRequestPermissions() {
        if (checkPermission()) {
            getCurrentLocation()
        } else {
            requestPermissions()
        }
    }

    private fun getCurrentLocation() {
        if (isLocationEnabled()) {
            if (ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions()
                return
            }
            locationProviderClient.lastLocation.addOnSuccessListener(activity) { location ->
                if (location == null) {
                    Toast.makeText(activity, "Failed to get location. Make sure location is enabled on the device.", Toast.LENGTH_SHORT).show()
                } else {
                    val geocoder = Geocoder(activity, Locale.getDefault())
                    val addresses: List<Address>? = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                    val cityName = addresses?.getOrNull(0)?.locality
                    if (cityName != null) {
                        Toast.makeText(activity, "Location acquired: $cityName", Toast.LENGTH_SHORT).show()
                        navigateToWeatherActivity(location.latitude, location.longitude, cityName)
                    } else {
                        Toast.makeText(activity, "City name not found", Toast.LENGTH_SHORT).show()
                    }
                }
            }.addOnFailureListener(activity) { exception ->
                Toast.makeText(activity, "Failed to get location: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(activity, "Location services are turned off. Turn on location services.", Toast.LENGTH_SHORT).show()
            activity.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
        }
    }

    private fun navigateToWeatherActivity(latitude: Double, longitude: Double, cityName: String) {
        val weatherIntent = Intent(activity, WeatherActivity::class.java).apply {
            putExtra("latitude", latitude)
            putExtra("longitude", longitude)
            putExtra("cityName", cityName)
        }
        activity.startActivity(weatherIntent)
        activity.finish()
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager = activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            activity, arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ), PERMISSION_REQUEST_ACCESS_LOCATION
        )
    }

    fun onRequestPermissionsResult(requestCode: Int, grantResults: IntArray) {
        if (requestCode == PERMISSION_REQUEST_ACCESS_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(activity.applicationContext, "Permission granted", Toast.LENGTH_SHORT).show()
                getCurrentLocation()
            } else {
                Toast.makeText(activity.applicationContext, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    companion object {
        private const val PERMISSION_REQUEST_ACCESS_LOCATION = 100
    }
}
