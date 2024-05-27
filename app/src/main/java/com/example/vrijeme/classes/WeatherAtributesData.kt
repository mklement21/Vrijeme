package com.example.vrijeme.classes

data class WeatherAttributesData(
    val pressure: Int,
    val humidity: Int,
    val windSpeed: Double,
    val rainProbability: Double,
    val clouds: Int,
    val visibility: Int
)

