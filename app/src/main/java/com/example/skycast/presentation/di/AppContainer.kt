package com.example.skycast.presentation.di

import android.app.Application
import com.example.data.mapper.WeatherRepositoryMapper
import com.example.data.mapper.WeatherRepositoryMapperImpl
import com.example.data.repository.WeatherCache
import com.example.data.repository.WeatherCacheImpl
import com.example.data.repository.WeatherRepositoryImpl
import com.example.domain.repository.WeatherRepository

class AppContainer(application: Application) {
    private val weatherCache: WeatherCache = WeatherCacheImpl(application)
    private val weatherRepositoryMapper: WeatherRepositoryMapper = WeatherRepositoryMapperImpl()
    private val weatherRepository: WeatherRepository = WeatherRepositoryImpl(weatherRepositoryMapper = weatherRepositoryMapper,
                                                                             weatherCacheImpl = weatherCache)

    fun provideWeatherCache(): WeatherCache = weatherCache

    fun provideWeatherMapper(): WeatherRepositoryMapper = weatherRepositoryMapper

    fun provideWeatherRepository(): WeatherRepository = weatherRepository
}