package com.example.vrijeme

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.vrijeme.helpers.LocationHelper
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import com.example.vrijeme.helpers.WeatherAlarmReceiver
import java.util.*

class MainActivity : ComponentActivity() {
    private lateinit var locationHelper: LocationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationHelper = LocationHelper(this)
        locationHelper.checkAndRequestPermissions()

        scheduleNotification()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        locationHelper.onRequestPermissionsResult(requestCode, grantResults)
    }

    private fun scheduleNotification() {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, WeatherAlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 12)
        calendar.set(Calendar.MINUTE, 45)
        calendar.set(Calendar.SECOND, 20)

        if (calendar.timeInMillis < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }
        Log.d("MainActivity", "Alarm set for: ${calendar.time}")

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
    }
}
