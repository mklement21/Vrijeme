package com.example.vrijeme.services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.vrijeme.R
import com.example.vrijeme.helpers.WeatherDataManager

private const val CHANNEL_ID = "WeatherChannel"

class WeatherNotificationService : Service() {
    private val NOTIFICATION_ID = 100

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        startForegroundService()
    }

    private fun startForegroundService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Weather Notification Channel",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Channel for weather notifications"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(channel)
        }

        val notification: Notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Weather Notifications")
            .setContentText("Fetching latest weather updates")
            .setSmallIcon(R.drawable.ic_notification)
            .build()

        startForeground(NOTIFICATION_ID, notification)
    }

    private fun sendNotification(temperature: String, description: String) {
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Weather Update")
            .setContentText("Current temperature: $temperature°C. Forecast: $description")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = getSystemService(NotificationManager::class.java)
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun getWeatherDataFromApi() {
        val cityName = "Zagreb" // Ili neki drugi grad
        WeatherDataManager.getWeatherData(cityName, getString(R.string.api_key)) { weatherData ->
            if (weatherData != null) {
                val temperature = weatherData.list[0].main.temp.toString()
                val description = weatherData.list[0].weather.firstOrNull()?.description ?: "No data"
                sendNotification(temperature, description)
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        getWeatherDataFromApi()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
