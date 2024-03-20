package com.example.data.repository

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.domain.models.WeatherModel
import com.example.domain.repository.WeatherCache
import com.google.gson.Gson

class WeatherCacheImpl(application: Application): WeatherCache {

    private val sharedPreferences: SharedPreferences = application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "WeatherPrefs"
        private const val KEY_WEATHER_DATA = "weatherData"
        private val gson = Gson()
    }

    override fun saveWeatherDataInCache(weatherModel: WeatherModel) {
        val json = gson.toJson(weatherModel)
        sharedPreferences.edit().putString(KEY_WEATHER_DATA, json).apply()
    }

    override fun getWeatherDataFromCache(): WeatherModel? {
        val json = sharedPreferences.getString(KEY_WEATHER_DATA, null)
        return gson.fromJson(json, WeatherModel::class.java)
    }
}