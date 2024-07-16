package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.domain.models.WeatherModel
import com.google.gson.Gson
import javax.inject.Inject

interface WeatherCache {
    /**
     * Saves weather data in the cache.
     * @param weatherModel The weather data model to be saved.
     */
    fun saveWeatherDataInCache(weatherModel: WeatherModel)
    /**
     * Retrieves weather data from the cache.
     * @return The cached weather data model, or null if not found.
     */
    fun getWeatherDataFromCache(): WeatherModel?
}

class WeatherCacheImpl @Inject constructor(val context: Context): WeatherCache {

    // SharedPreferences instance to store cached data
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "WeatherPrefs" // SharedPreferences file name
        private const val KEY_WEATHER_DATA = "weatherData" // Key to store weather data in SharedPreferences
        private val gson = Gson() // Gson instance for JSON serialization/deserialization
    }

    /**
     * Saves weather data in SharedPreferences cache.
     * @param weatherModel The weather data model to be saved.
     */
    override fun saveWeatherDataInCache(weatherModel: WeatherModel) {
        val json = gson.toJson(weatherModel)
        sharedPreferences.edit().putString(KEY_WEATHER_DATA, json).apply()
    }

    /**
     * Retrieves weather data from SharedPreferences cache.
     * @return The cached weather data model, or null if not found.
     */
    override fun getWeatherDataFromCache(): WeatherModel? {
        val json = sharedPreferences.getString(KEY_WEATHER_DATA, null)
        return gson.fromJson(json, WeatherModel::class.java)
    }
}