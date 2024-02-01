package com.example.domain.usecase

import com.example.domain.models.WeatherModel
import com.example.domain.repository.WeatherRepository

class GetWeatherDataUseCase(
    private val weatherRepository: WeatherRepository
) {
    suspend fun execute(params: String, callback: (WeatherModel?) -> Unit) {
        try {
            val weatherModel = weatherRepository.getWeatherData(params)
            callback(weatherModel)
        } catch (e: Exception) {
            callback(null)
        }
    }
}