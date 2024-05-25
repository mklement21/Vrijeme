package com.example.vrijeme.classes

data class WeatherData(
    val list: List<ForecastData>,
    val city: CityData,
)

data class ForecastData(
    val dt: Long,
    val main: MainData,
    val weather: List<Weather>,
    val dt_txt: String
)

data class MainData(
    val temp: Double,
    val feels_like: Double,
    val temp_min: Double,
    val temp_max: Double,
    val humidity: Int
)

data class Weather(
    val main: String,
    val description: String
)

data class CityData(
    val id: Int,
    val name: String,
    val coord: CoordData,
    val country: String,
    val population: Int,
    val timezone: Int,
    val sunrise: Long,
    val sunset: Long
)

data class CoordData(
    val lat: Double,
    val lon: Double
)