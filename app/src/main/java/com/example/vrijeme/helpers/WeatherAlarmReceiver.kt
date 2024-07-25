package com.example.vrijeme.helpers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.vrijeme.services.WeatherNotificationService

class WeatherAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val serviceIntent = Intent(context, WeatherNotificationService::class.java)
        context.startService(serviceIntent)
    }
}