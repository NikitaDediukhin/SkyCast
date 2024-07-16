package com.example.domain.repository

import com.example.domain.models.WeatherModel

/**
 * Repository interface for fetching weather data.
 */
interface WeatherRepository {
    /**
     * Fetches weather data for a specific city.
     * @param cityName The name of the city for which weather data is requested.
     * @param permits Indicates whether the app has necessary permissions for location access.
     * @return WeatherModel containing the weather information for the city, or null if the data couldn't be retrieved.
     */
    suspend fun getWeatherData(cityName: String, permits: Boolean): WeatherModel?
}