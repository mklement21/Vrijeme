package com.example.vrijeme.services

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import com.example.vrijeme.R
import com.example.vrijeme.helpers.WeatherDataManager
import retrofit2.Call
import retrofit2.Callback
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import androidx.core.app.NotificationCompat

private const val API_BASE_URL = "https://api.example.com/"

class WeatherNotificationService : Service() {
    private val NOTIFICATION_ID = 100
    private val CHANNEL_ID = "WeatherChannel"

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        getWeatherDataFromApi()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Weather Service")
            .setContentText("Fetching weather updates...")
            .setSmallIcon(R.drawable.ic_notification)
            .build()

        startForeground(NOTIFICATION_ID, notification)

        getWeatherDataFromApi()
        return START_STICKY
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

    private fun sendNotification(temperature: String) {
        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Weather Alert")
            .setContentText("Current temperature: $temperature °C")
            .setSmallIcon(R.drawable.ic_notification)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    private fun getWeatherDataFromApi() {
        val cityName = "Zagreb"
        WeatherDataManager.getWeatherData(cityName, getString(R.string.api_key)) { weatherData ->
            if (weatherData != null) {
                val temperature = weatherData.list[0].main.temp.toString()
                sendNotification(temperature)
            } else {
                Log.e("WeatherNotification", "Failed to retrieve weather data for $cityName")
            }
        }
    }
}
