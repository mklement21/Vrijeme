package com.example.vrijeme.helpers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class WeatherAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("WeatherAlarmReceiver", "Alarm received")
        Toast.makeText(context, "Alarm je zazvonio!", Toast.LENGTH_SHORT).show()
    }
}
