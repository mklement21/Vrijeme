package com.example.vrijeme

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.example.vrijeme.classes.WeatherData
import com.example.vrijeme.helpers.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class WeatherActivity : ComponentActivity() {

    private lateinit var searchCity: EditText
    private lateinit var searchButton: Button
    private lateinit var dateLabel: TextView
    private lateinit var cityLabel: TextView
    private lateinit var temperatureLabel: TextView
    private lateinit var tempMinLabel: TextView
    private lateinit var tempMaxLabel: TextView
    private lateinit var tempFeelsLikeLabel: TextView
    private lateinit var descriptionLabel: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_weather)

        searchCity = findViewById(R.id.searchCity)
        searchButton = findViewById(R.id.searchButton)
        dateLabel = findViewById(R.id.dateLabel)
        cityLabel = findViewById(R.id.cityLabel)
        temperatureLabel = findViewById(R.id.temperatureLabel)
        tempMinLabel = findViewById(R.id.tempMinLabel)
        tempMaxLabel = findViewById(R.id.tempMaxLabel)
        tempFeelsLikeLabel = findViewById(R.id.tempFeelsLikeLabel)
        descriptionLabel = findViewById(R.id.descriptionLabel)

        searchButton.setOnClickListener {
            val city = searchCity.text.toString()
            if (city.isNotEmpty()) {
                getWeatherData(city)
            } else {
                Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getWeatherData(city: String) {
        val call = RetrofitInstance.api.getWeatherForecast(city, getString(R.string.api_key))
        call.enqueue(object : Callback<WeatherData> {
            override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                if (response.isSuccessful) {
                    response.body()?.let { weatherData ->
                        updateUI(weatherData)
                    }
                } else {
                    Toast.makeText(this@WeatherActivity, "Failed to retrieve data", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<WeatherData>, t: Throwable) {
                Toast.makeText(this@WeatherActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUI(weatherData: WeatherData) {
        val city = weatherData.city
        val currentWeather = weatherData.list.firstOrNull()

        if (currentWeather != null) {
            cityLabel.text = city.name
            dateLabel.text = SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                Locale.getDefault()
            ).format(Date(currentWeather.dt * 1000))
            temperatureLabel.text = "${currentWeather.main.temp}°C"
            tempMinLabel.text = "Min: ${currentWeather.main.temp_min}°C"
            tempMaxLabel.text = "Max: ${currentWeather.main.temp_max}°C"
            tempFeelsLikeLabel.text = "Feels Like: ${currentWeather.main.feels_like}°C"
            descriptionLabel.text = currentWeather.weather.firstOrNull()?.description ?: ""
        }

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}