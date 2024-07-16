package com.example.domain.usecase

import com.example.domain.models.WeatherModel
import com.example.domain.repository.WeatherRepository

/**
 * Use case class for retrieving weather data.
 * @param weatherRepository The repository instance used to get weather data.
 */
class GetWeatherDataUseCase(
    private val weatherRepository: WeatherRepository
) {
    /**
     * Executes the use case to get weather data.
     * @param params The parameters for the weather query, typically a city name.
     * @param permits A flag indicating whether to allow network requests or use cached data.
     * @param callback The callback function to handle the resulting WeatherModel or null if an error occurs.
     */
    suspend fun execute(params: String, permits: Boolean, callback: (WeatherModel?) -> Unit) {
        try {
            // Attempt to get weather data from the repository
            val weatherModel = weatherRepository.getWeatherData(params, permits)
            // Pass the resulting WeatherModel to the callback function
            callback(weatherModel)
        } catch (e: Exception) {
            // In case of an exception, pass null to the callback function
            callback(null)
        }
    }
}