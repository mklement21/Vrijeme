package com.example.vrijeme.services

import android.app.IntentService
import android.content.Intent
import com.example.vrijeme.R
import com.example.vrijeme.classes.WeatherData
import com.example.vrijeme.helpers.WeatherDataManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val API_BASE_URL = "https://api.example.com/"

class WeatherNotificationService : IntentService("WeatherNotificationService") {
    override fun onHandleIntent(intent: Intent?) {
        getWeatherDataFromApi()
    }

    private fun getWeatherDataFromApi() {
        val cityName = " Zagreb"
        WeatherDataManager.getWeatherData(cityName, getString(R.string.api_key)) { weatherData ->
            if (weatherData != null) {
            } else {
            }
        }
    }
}