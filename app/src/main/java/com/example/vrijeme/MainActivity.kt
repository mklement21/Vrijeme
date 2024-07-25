package com.example.vrijeme

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.vrijeme.helpers.LocationHelper
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.vrijeme.helpers.WeatherAlarmReceiver
import java.util.*



class MainActivity : ComponentActivity() {
    private lateinit var locationHelper: LocationHelper
    private val NOTIFICATION_PERMISSION_REQUEST_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationHelper = LocationHelper(this)
        locationHelper.checkAndRequestPermissions()

       // checkAndRequestNotificationPermission()
        scheduleNotification()
    }

    private fun checkAndRequestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val notificationPermission = android.Manifest.permission.POST_NOTIFICATIONS
            val permissionCheck = ContextCompat.checkSelfPermission(this, notificationPermission)

            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(notificationPermission), NOTIFICATION_PERMISSION_REQUEST_CODE)
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == NOTIFICATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Dozvola je dodijeljena
            } else {
                // Dozvola nije dodijeljena
                Toast.makeText(this, "Notification permission is required", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun scheduleNotification() {
        Log.d("MainActivity", "Scheduling notification")
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, WeatherAlarmReceiver::class.java).apply {
            // Dodajte dodatke ako je potrebno
        }
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 18)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        // Ako je vreme prošlo za danas, pomerimo za sutra
        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }
}
