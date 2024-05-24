package com.example.vrijeme.helpers
import com.example.vrijeme.classes.WeatherData
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherApi {

    //https://api.openweathermap.org/data/2.5/forecast?q=Zagreb&appid=df70babbb87f5d879808ee33ec5685f6&units=metric
    @GET("forecast")
    fun getWeatherForecast(
        @Query("q") city: String,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): Call<ResponseBody>
}