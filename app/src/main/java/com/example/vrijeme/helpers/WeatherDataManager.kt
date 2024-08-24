package com.example.vrijeme.helpers

import com.example.vrijeme.classes.WeatherData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

object WeatherDataManager {
    fun getWeatherData(city: String, apiKey: String, callback: (WeatherData?) -> Unit) {
        val call = RetrofitInstance.api.getWeatherForecast(city, apiKey)
        call.enqueue(object : Callback<WeatherData> {
            override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                if (response.isSuccessful) {
                    response.body()?.let { weatherData ->
                        callback(weatherData)
                    }
                } else {
                    callback(null)
                }
            }

            override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                callback(null)
            }
        })
    }
}