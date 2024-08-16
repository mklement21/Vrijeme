package com.example.vrijeme.helpers
import com.example.vrijeme.classes.WeatherData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherApi {
    @GET("forecast")
    fun getWeatherForecast(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Call<WeatherData>
}
