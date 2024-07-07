package com.example.data.repository

import android.util.Log
import com.example.data.BuildConfig
import com.example.data.mapper.WeatherRepositoryMapper
import com.example.data.response.WeatherResponse
import com.example.data.service.WeatherApiService
import com.example.domain.models.WeatherModel
import com.example.domain.repository.WeatherRepository
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class WeatherRepositoryImpl(
    private var weatherApiService: WeatherApiService = initApi(),
    private val weatherRepositoryMapper: WeatherRepositoryMapper,
    private val weatherCacheImpl: WeatherCache,
): WeatherRepository {

    companion object {
        fun initApi(): WeatherApiService {
            val gson = GsonBuilder().create()
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.weatherapi.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            return retrofit.create(WeatherApiService::class.java)
        }
    }

    override suspend fun getWeatherData(cityName: String, permits: Boolean): WeatherModel? {
        val weatherModel: WeatherModel?
        val weatherResponse: WeatherResponse?

        if (permits){
            try {
                val apiKey = BuildConfig.WEATHER_API_KEY
                val response = withContext(Dispatchers.IO) {
                    try {
                        weatherApiService.getWeatherInfo(apiKey, cityName)
                    }
                    catch (e: Exception) {
                        Log.e("ErrorRepository", "Response error $e")
                        null
                    }
                }

                Log.w("query", response?.raw()?.request().toString())

                if (response != null) {
                    if (response.isSuccessful) {
                        weatherResponse = response.body()
                        if (weatherResponse?.forecast?.forecastday != null && weatherResponse.forecast.forecastday.isNotEmpty()) {
                            weatherModel = weatherRepositoryMapper.toWeatherModel(weatherResponse)
                            weatherCacheImpl.saveWeatherDataInCache(weatherModel)
                            return weatherModel
                        } else {
                            Log.e("ErrorRepository", "Empty response or missing forecast data, response = ${response.body()}")
                        }
                    } else {
                        Log.e("ErrorRepository", "Unsuccessful API response: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                Log.e("ErrorRepository", e.toString())
            }
        } else {
            weatherModel = weatherCacheImpl.getWeatherDataFromCache()
            if (weatherModel != null){
                return weatherModel
            }
        }


        return null
    }
}