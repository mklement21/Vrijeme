package com.example.vrijeme.helpers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.content.ContextCompat
import com.example.vrijeme.services.LocationService

class WeatherAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, LocationService::class.java)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Log.d("WeatherAlarmReceiver", "Starting service in foreground mode")
            ContextCompat.startForegroundService(context, serviceIntent)
        } else {
            Log.d("WeatherAlarmReceiver", "Starting service in background mode")
            context.startService(serviceIntent)
        }
    }
}
