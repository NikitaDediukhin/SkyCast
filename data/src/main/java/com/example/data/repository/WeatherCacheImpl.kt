package com.example.data.repository

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.example.domain.models.WeatherModel
import com.google.gson.Gson
import javax.inject.Inject

interface WeatherCache {
    fun saveWeatherDataInCache(weatherModel: WeatherModel)
    fun getWeatherDataFromCache(): WeatherModel?
}

class WeatherCacheImpl @Inject constructor(val context: Context): WeatherCache {

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

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