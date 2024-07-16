package com.example.data.service

import com.example.data.response.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

const val ALERTS = "no"
const val AQI = "no"
const val DAYS = 7
const val LANGUAGE = "ru"

/**
 * Interface defining the Weather API service endpoints.
 */

interface WeatherApiService {

    /**
     * Fetch weather information for a specific city from weatherapi.com.
     *
     * @param apiKey API key for accessing the weather API
     * @param city Name of the city for which weather information is requested
     * @param days Number of days to retrieve forecast (default is 7 days)
     * @param aqi Whether to include Air Quality Index (AQI) information ("yes" or "no", default is "no")
     * @param alerts Whether to include weather alerts ("yes" or "no", default is "no")
     * @param language Language code for the response (default is "ru" for Russian)
     */
    @GET("v1/forecast.json")
    suspend fun getWeatherInfo(
        @Query("key") apiKey: String,
        @Query("q") city: String,
        @Query("days") days: Int = DAYS,
        @Query("aqi") aqi: String = AQI,
        @Query("alerts") alerts: String = ALERTS,
        @Query("lang") language: String = LANGUAGE
    ): Response<WeatherResponse>
}