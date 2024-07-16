package com.example.domain.models

/**
 * Data class representing the overall weather information including current and daily forecasts.
 * @property currentWeather Current weather details including city, time, temperatures, condition, and icon.
 * @property dailyWeather List of daily weather forecasts including date, temperatures, condition, and hourly details.
 */
data class WeatherModel(
    val currentWeather: CurrentWeather,
    val dailyWeather: List<DailyWeather>,
) {
    data class CurrentWeather(
        val city: String,
        val time: String,
        val avgTemp: Int,
        val maxTemp: Int,
        val minTemp: Int,
        val condition: String,
        val icon: String
    )

    data class DailyWeather(
        val day: String,
        val icon: String,
        val avgTemp: Int,
        val maxTemp: Int,
        val minTemp: Int,
        val condition: String,
        val hourlyWeather: List<HourlyWeather>
    )

    data class HourlyWeather(
        val time: String,
        val icon: String,
        val avgTemp: Int
    )
}