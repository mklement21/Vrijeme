package com.example.vrijeme.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.example.vrijeme.R
import com.example.vrijeme.helpers.LocationHelper

class LocationService : Service() {
    private val NOTIFICATION_ID = 2
    private val CHANNEL_ID = "LocationChannel"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        Log.d("LocationService", "Service created")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Location Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val initialNotification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Location Service")
            .setContentText("Initializing...")
            .setSmallIcon(R.drawable.ic_notification)
            .build()
        startForeground(NOTIFICATION_ID, initialNotification)

        fetchAndSendLocation()

        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun fetchAndSendLocation() {
        val locationHelper = LocationHelper(this)
        locationHelper.checkAndRequestPermissions { latitude, longitude, cityName ->
            Log.d("LocationService", "Fetched location: $latitude, $longitude, City: $cityName")
            updateNotification(cityName)
            sendLocationToWeatherService(cityName, latitude, longitude)
        }
    }

    private fun updateNotification(cityName: String) {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Location Service")
            .setContentText("Tracking location in $cityName")
            .setSmallIcon(R.drawable.ic_notification)
            .build()

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notification)
    }

    private fun sendLocationToWeatherService(cityName: String, latitude: Double, longitude: Double) {
        Log.d("LocationService", "Sending location to WeatherNotificationService: $cityName")

        val weatherIntent = Intent(this, WeatherNotificationService::class.java).apply {
            putExtra("cityName", cityName)
            putExtra("latitude", latitude)
            putExtra("longitude", longitude)
        }
        ContextCompat.startForegroundService(this, weatherIntent)
    }
}