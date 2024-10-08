package com.example.vrijeme

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.vrijeme.adapters.TodayWeatherAdapter
import com.example.vrijeme.adapters.WeatherAttribute
import com.example.vrijeme.adapters.WeatherAttributeAdapter
import com.example.vrijeme.adapters.WeekWeatherAdapter
import com.example.vrijeme.classes.TodayWeatherItem
import com.example.vrijeme.classes.WeatherAttributesData
import com.example.vrijeme.classes.WeatherData
import com.example.vrijeme.classes.WeekWeatherItem
import com.example.vrijeme.helpers.RetrofitInstance
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
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
    private var weatherData: WeatherData? = null

    private lateinit var recyclerViewToday: RecyclerView
    private lateinit var recyclerViewWeek: RecyclerView
    private lateinit var recyclerViewAttributes: RecyclerView

    private lateinit var weekWeatherAdapter: WeekWeatherAdapter
    private lateinit var weatherAttributeAdapter: WeatherAttributeAdapter

    private val weekForecastList = mutableListOf<WeekWeatherItem>()
    private lateinit var locationProviderClient: FusedLocationProviderClient

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

        recyclerViewToday = findViewById(R.id.recyclerViewToday)
        recyclerViewToday.layoutManager = LinearLayoutManager(this)
        recyclerViewToday.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        recyclerViewWeek = findViewById(R.id.recyclerViewWeek)
        recyclerViewWeek.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        weekWeatherAdapter = WeekWeatherAdapter(weekForecastList)
        recyclerViewWeek.adapter = weekWeatherAdapter

        recyclerViewAttributes = findViewById(R.id.recyclerViewAttributes)
        recyclerViewAttributes.layoutManager = GridLayoutManager(this, 2)

        locationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        val cityName = intent.getStringExtra("cityName") ?: ""
        if (cityName.isNotEmpty()) {
            cityLabel.text = cityName
            getWeatherData(cityName)
        }

        searchButton.setOnClickListener {
            val city = searchCity.text.toString()
            if (city.isNotEmpty()) {
                getWeatherData(city)
            } else {
                Toast.makeText(this, "Please enter a city name", Toast.LENGTH_SHORT).show()
            }
        }

        weekWeatherAdapter.setOnItemClickListener { position ->
            val selectedDayWeather = weekForecastList[position]
            val selectedDate = selectedDayWeather.date
            Log.d("WeatherActivity", "Selected Date: $selectedDate")

            weatherData?.let {
                displayDetailedDataForDate(selectedDate)
                updateTodayWeatherForSelectedDay(selectedDate)
            } ?: run {
                Toast.makeText(this, "Weather data not available", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun getWeatherData(city: String) {
        val call = RetrofitInstance.api.getWeatherForecast(city, getString(R.string.api_key))
        call.enqueue(object : Callback<WeatherData> {
            override fun onResponse(call: Call<WeatherData>, response: Response<WeatherData>) {
                if (response.isSuccessful) {
                    response.body()?.let { weatherData ->
                        this@WeatherActivity.weatherData = weatherData
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
        val todayWeatherData = weatherData.list.filter { isToday(it.dt) }

        if (todayWeatherData.isNotEmpty()) {
            val currentWeather = todayWeatherData.first()
            val tempMin = todayWeatherData.minOf { it.main.temp_min }
            val tempMax = todayWeatherData.maxOf { it.main.temp_max }

            cityLabel.text = city.name
            dateLabel.text = SimpleDateFormat(
                "dd/MM/yyyy HH:mm",
                Locale.getDefault()
            ).format(Date(currentWeather.dt * 1000))
            temperatureLabel.text = "${currentWeather.main.temp}°C"
            tempMinLabel.text = "Min: ${tempMin}°C"
            tempMaxLabel.text = "Max: ${tempMax}°C"
            tempFeelsLikeLabel.text = "Feels Like: ${currentWeather.main.feels_like}°C"
            descriptionLabel.text =
                currentWeather.weather.firstOrNull()?.description?.capitalize() ?: ""

            recyclerViewToday.adapter = TodayWeatherAdapter(todayWeatherData.map {
                TodayWeatherItem(
                    time = SimpleDateFormat(
                        "HH:mm",
                        Locale.getDefault()
                    ).format(Date(it.dt * 1000)),
                    temperature = "${it.main.temp}",
                    description = it.weather.firstOrNull()?.description?.capitalize() ?: ""
                )
            })

            // Attributes
            if (weatherData.list.isNotEmpty()) {
                val attributesData = WeatherAttributesData(
                    pressure = currentWeather.main.pressure,
                    humidity = currentWeather.main.humidity,
                    windSpeed = currentWeather.wind.speed,
                    rainProbability = currentWeather.rain?.next3h ?: 0.0,
                    clouds = currentWeather.clouds.all,
                    visibility = currentWeather.visibility
                )

                val attributesList = listOf(
                    WeatherAttribute("Pressure", "${attributesData.pressure} hPa"),
                    WeatherAttribute("Humidity", "${attributesData.humidity} %"),
                    WeatherAttribute("Wind Speed", "${attributesData.windSpeed} m/s"),
                    WeatherAttribute("Rain Probability", "${attributesData.rainProbability} mm"),
                    WeatherAttribute("Clouds", "${attributesData.clouds} %"),
                    WeatherAttribute("Visibility", "${attributesData.visibility} m")
                )
                weatherAttributeAdapter = WeatherAttributeAdapter(attributesList)
                recyclerViewAttributes.adapter = weatherAttributeAdapter
            }
        }

        // Week data
        val weekForecastList = weatherData.list.groupBy { forecast ->
            SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date(forecast.dt * 1000))
        }.map { (date, forecasts) ->
            val tempMin = forecasts.minOf { it.main.temp_min }
            val tempMax = forecasts.maxOf { it.main.temp_max }
            val description = forecasts.firstOrNull()?.weather?.firstOrNull()?.description ?: ""
            WeekWeatherItem(
                date = date,
                tempMin = "$tempMin",
                tempMax = "$tempMax",
                description = description.capitalize()
            )
        }

        this.weekForecastList.clear()
        this.weekForecastList.addAll(weekForecastList)
        weekWeatherAdapter.notifyDataSetChanged()
    }

    private fun displayDetailedDataForDate(date: String) {
        val forecastsForDate = weatherData?.list?.filter { it.dt_txt.startsWith(date) }

        if (forecastsForDate != null && forecastsForDate.isNotEmpty()) {
            val currentWeather = forecastsForDate.first()

            val tempMin = forecastsForDate.minOf { it.main.temp_min }
            val tempMax = forecastsForDate.maxOf { it.main.temp_max }
            val temperature = forecastsForDate[0].main.temp
            val feelsLike = forecastsForDate[0].main.feels_like
            val descriptionWeather = forecastsForDate[0].weather[0].description

            dateLabel.text = date
            temperatureLabel.text = "${temperature}°C"
            tempMinLabel.text = "Min: ${tempMin}°C"
            tempMaxLabel.text = "Max: ${tempMax}°C"
            tempFeelsLikeLabel.text = "Feels Like: ${feelsLike}°C"
            descriptionLabel.text = descriptionWeather.capitalize() ?: ""

            val attributesData = WeatherAttributesData(
                pressure = currentWeather.main.pressure,
                humidity = currentWeather.main.humidity,
                windSpeed = currentWeather.wind.speed,
                rainProbability = currentWeather.rain?.next3h ?: 0.0,
                clouds = currentWeather.clouds.all,
                visibility = currentWeather.visibility
            )

            val attributesList = listOf(
                WeatherAttribute("Pressure", "${attributesData.pressure} hPa"),
                WeatherAttribute("Humidity", "${attributesData.humidity} %"),
                WeatherAttribute("Wind Speed", "${attributesData.windSpeed} m/s"),
                WeatherAttribute("Rain Probability", "${attributesData.rainProbability} mm"),
                WeatherAttribute("Clouds", "${attributesData.clouds} %"),
                WeatherAttribute("Visibility", "${attributesData.visibility} m")
            )

            weatherAttributeAdapter = WeatherAttributeAdapter(attributesList)
            recyclerViewAttributes.adapter = weatherAttributeAdapter

        } else {
            Log.d("WeatherActivity", "No forecast data available for date: $date")
        }
        Log.d("WeatherActivity", "Forecasts for selected date: $forecastsForDate")
    }

    private fun updateTodayWeatherForSelectedDay(selectedDate: String) {
        val selectedDayWeather = weatherData?.list?.filter { it.dt_txt.startsWith(selectedDate) }

        if (selectedDayWeather != null && selectedDayWeather.isNotEmpty()) {
            recyclerViewToday.adapter = TodayWeatherAdapter(selectedDayWeather.map {
                TodayWeatherItem(
                    time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(it.dt * 1000)),
                    temperature = "${it.main.temp}",
                    description = it.weather.firstOrNull()?.description?.capitalize() ?: ""
                )
            })
        } else {
            Log.d("WeatherActivity", "No forecast data available for selected date: $selectedDate")
        }
    }

    private fun isToday(timestamp: Long): Boolean {
        val date = Date(timestamp * 1000)
        val today = Date()
        val sdf = SimpleDateFormat("yyyyMMdd", Locale.getDefault())
        return sdf.format(date) == sdf.format(today)
    }
}