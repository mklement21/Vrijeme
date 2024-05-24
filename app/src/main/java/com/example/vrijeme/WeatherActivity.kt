package com.example.vrijeme

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.vrijeme.helpers.RetrofitInstance
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherActivity : ComponentActivity() {
    private lateinit var searchCity: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        val apiKey = "df70babbb87f5d879808ee33ec5685f6"

        searchCity = findViewById(R.id.searchCity)

        val searchButton = findViewById<Button>(R.id.searchButton)
        searchButton.setOnClickListener {
            val city = searchCity.text.toString().trim()
            if (city.isNotEmpty()) {
                fetchData(city, apiKey)
            } else {
                showToast("Enter city name")
            }
        }
    }

    private fun fetchData(city: String, apiKey: String) {
        RetrofitInstance.api.getWeatherForecast(city, apiKey)
            .enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        val rawResponse = response.body()?.string()
                        Log.d("WeatherActivity", "Raw JSON response: $rawResponse")

                    } else {
                        showToast("Error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    showToast("Failure: ${t.message}")
                }
            })
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}