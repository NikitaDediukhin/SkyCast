package com.example.domain.usecase

import com.example.domain.models.WeatherModel
import com.example.domain.repository.WeatherRepository

class RefreshWeatherDataUseCase(
    private val weatherRepository: WeatherRepository
) {
    suspend fun execute(city: String, callback: (WeatherModel?) -> Unit) {
        try {
            val weatherModel = weatherRepository.getWeatherData(city)
            callback(weatherModel)
        } catch (e: Exception) {
            callback(null)
        }
    }
}