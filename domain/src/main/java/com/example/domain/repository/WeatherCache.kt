package com.example.domain.repository

import com.example.domain.models.WeatherModel

interface WeatherCache {
    fun saveWeatherDataInCache(weatherModel: WeatherModel)
    fun getWeatherDataFromCache(): WeatherModel?
}