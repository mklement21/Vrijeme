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
import com.example.vrijeme.helpers.WeatherDataManager

class WeatherNotificationService : Service() {
    private val NOTIFICATION_ID = 100
    private val CHANNEL_ID = "WeatherChannel"
    private lateinit var cityName: String

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Weather Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        cityName = intent?.getStringExtra("cityName") ?: "Unknown"
        Log.d("WeatherNotificationService", "City name received: $cityName")

        updateWeatherData(cityName)

        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun sendNotification(temperature: String) {
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Weather Alert")
            .setContentText("Current temperature in $cityName: $temperature °C")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())

        stopForeground(true)
        stopSelf()
    }

    private fun updateWeatherData(city: String) {
        Log.d("WeatherNotification", "Updating weather data for city: $city")
        WeatherDataManager.getWeatherData(city, getString(R.string.api_key)) { weatherData ->
            if (weatherData != null) {
                val temperature = weatherData.list[0].main.temp.toString()
                Log.d("WeatherNotification", "Weather data fetched successfully: $temperature °C")
                sendNotification(temperature)
            } else {
                Log.e("WeatherNotification", "Failed to retrieve weather data")
                sendErrorNotification()
            }
        }
    }

    private fun sendErrorNotification() {
        val errorNotificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Weather Alert")
            .setContentText("Failed to retrieve weather data.")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, errorNotificationBuilder.build())

        stopForeground(true)
        stopSelf()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}