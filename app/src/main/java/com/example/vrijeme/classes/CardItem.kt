package com.example.vrijeme.classes

data class TodayWeatherItem(
    val time: String,
    val temperature: String,
    val description: String
)

data class WeekWeatherItem(
    val date: String,
    val tempMin: String,
    val tempMax: String,
    val description: String
)