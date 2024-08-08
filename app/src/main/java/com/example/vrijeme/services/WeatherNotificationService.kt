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
import com.example.vrijeme.R
import com.example.vrijeme.helpers.LocationHelper
import com.example.vrijeme.helpers.WeatherDataManager
import com.example.vrijeme.helpers.WeatherDataManager.getWeatherData

class WeatherNotificationService : Service() {
    private val NOTIFICATION_ID = 100
    private val CHANNEL_ID = "WeatherChannel"
    private lateinit var locationHelper: LocationHelper

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        locationHelper = LocationHelper(this)
        locationHelper.checkAndRequestPermissions { latitude, longitude, cityName ->
            onLocationReceived(latitude, longitude, cityName)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Weather Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun sendNotification(temperature: String) {
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Weather Alert")
            .setContentText("Current temperature: $temperature °C")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun updateWeatherData(city: String) {
        WeatherDataManager.getWeatherData(city, getString(R.string.api_key)) { weatherData ->
            if (weatherData != null) {
                val temperature = weatherData.list[0].main.temp.toString()
                sendNotification(temperature)
            } else {
                Log.e("WeatherNotification", "Failed to retrieve weather data for location")
            }
        }
    }

    fun onLocationReceived(latitude: Double, longitude: Double, cityName: String) {
        updateWeatherData(cityName)
    }
}

