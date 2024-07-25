package com.example.vrijeme.helpers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.vrijeme.services.WeatherNotificationService
import java.util.Calendar

class WeatherAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("WeatherAlarmReceiver", "Alarm received at ${Calendar.getInstance().time}")
        val serviceIntent = Intent(context, WeatherNotificationService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent)
        } else {
            context.startService(serviceIntent)
        }
    }
}
