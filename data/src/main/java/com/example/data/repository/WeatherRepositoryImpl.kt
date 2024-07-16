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

/**
 * Implementation of WeatherRepository that interacts with Weather API service
 * and manages weather data caching.
 *
 * @property weatherApiService The Retrofit service interface for making API requests.
 * @property weatherRepositoryMapper Mapper to convert API responses to domain models.
 * @property weatherCacheImpl Cache implementation to store and retrieve weather data.
 */
class WeatherRepositoryImpl(
    private var weatherApiService: WeatherApiService = initApi(),
    private val weatherRepositoryMapper: WeatherRepositoryMapper,
    private val weatherCacheImpl: WeatherCache,
): WeatherRepository {

    companion object {
        /**
         * Initialize the WeatherApiService with Retrofit.
         *
         * @return Initialized WeatherApiService instance.
         */
        fun initApi(): WeatherApiService {
            val gson = GsonBuilder().create()
            val retrofit = Retrofit.Builder()
                .baseUrl("https://api.weatherapi.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

            return retrofit.create(WeatherApiService::class.java)
        }
    }

    /**
     * Retrieves weather data for a specific city either from API or cache.
     *
     * @param cityName The name of the city for which weather data is requested.
     * @param permits Boolean flag indicating if API requests are permitted.
     * @return WeatherModel containing the weather data for the city, or null if data retrieval fails.
     */
    override suspend fun getWeatherData(cityName: String, permits: Boolean): WeatherModel? {
        val weatherModel: WeatherModel?
        val weatherResponse: WeatherResponse?

        if (permits){
            try {
                val apiKey = BuildConfig.WEATHER_API_KEY
                val response = withContext(Dispatchers.IO) {
                    try {
                        // Make API request to get weather info for the specified city
                        weatherApiService.getWeatherInfo(apiKey, cityName)
                    }
                    catch (e: Exception) {
                        Log.e("ErrorRepository", "Response error $e")
                        null
                    }
                }

                if (response != null) {
                    if (response.isSuccessful) {
                        weatherResponse = response.body()
                        if (weatherResponse?.forecast?.forecastday != null && weatherResponse.forecast.forecastday.isNotEmpty()) {
                            // Convert API response to domain model
                            weatherModel = weatherRepositoryMapper.toWeatherModel(weatherResponse)
                            // Save weather data to cache
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
            // Retrieve weather data from cache if API requests are not permitted
            weatherModel = weatherCacheImpl.getWeatherDataFromCache()
            if (weatherModel != null){
                return weatherModel
            }
        }

        return null
    }
}