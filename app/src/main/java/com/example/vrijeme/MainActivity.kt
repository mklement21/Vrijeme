package com.example.vrijeme

import android.app.AlarmManager
import android.app.AlarmManager.INTERVAL_DAY
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import com.example.vrijeme.helpers.LocationHelper
import com.example.vrijeme.helpers.WeatherAlarmReceiver
import java.util.*

class MainActivity : ComponentActivity() {
    private lateinit var locationHelper: LocationHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        locationHelper = LocationHelper(this)

        // Check permissions and request location
        locationHelper.checkAndRequestPermissions { latitude, longitude, cityName ->
            Log.d("MainActivity", "Location acquired: $latitude, $longitude, City: $cityName")
            val intent = Intent(this, WeatherActivity::class.java).apply {
                putExtra("latitude", latitude)
                putExtra("longitude", longitude)
                putExtra("cityName", cityName)
            }
            startActivity(intent)
            finish()
        }

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

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 9)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)

            if (timeInMillis < System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        Log.d("MainActivity", "Repeating alarm set for: ${calendar.time}")

        alarmManager.setExact(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            pendingIntent
        )
    }
}
