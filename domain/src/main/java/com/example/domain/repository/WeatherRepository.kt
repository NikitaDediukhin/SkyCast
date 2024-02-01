package com.example.domain.repository

import com.example.domain.models.WeatherModel

interface WeatherRepository {

    suspend fun getWeatherData(cityName: String): WeatherModel?

}