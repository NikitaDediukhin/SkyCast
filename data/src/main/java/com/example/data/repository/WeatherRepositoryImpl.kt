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
    private val weatherRepositoryMapper: WeatherRepositoryMapper
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

    override suspend fun getWeatherData(cityName: String): WeatherModel? {
        val weatherModel: WeatherModel?
        val weatherResponse: WeatherResponse?

        try {

            val apiKey = BuildConfig.API_KEY

            val response = withContext(Dispatchers.IO) {
                try {
                    weatherApiService.getWeatherInfo(apiKey, cityName)
                }
                catch (e: Exception) {
                    Log.e("ErrorRepository", "Response error $e")
                    null
                }
            }

            Log.w("Responseff", response.toString())

            if (response != null) {
                if (response.isSuccessful) {
                    weatherResponse = response.body()
                    if (weatherResponse?.forecast?.forecastday != null && weatherResponse.forecast.forecastday.isNotEmpty()) {
                        weatherModel = weatherRepositoryMapper.toWeatherModel(weatherResponse)
                        return weatherModel
                    } else {
                        // Обработка случая, когда ответ API пуст или не содержит прогноза
                        Log.e("ErrorRepository", "Empty response or missing forecast data, response = ${response.body()}")
                    }
                } else {
                    // Обработка случая, когда запрос к API не успешен
                    Log.e("ErrorRepository", "Unsuccessful API response: ${response.code()}")
                }
            }
        } catch (e: Exception) {
            // Обработка других исключений
            Log.e("ErrorRepository", e.toString())
        }
        return null
    }



}