package com.example.data.service

import com.example.data.response.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

const val ALERTS = "no"
const val AQI = "no"
const val DAYS = 7
const val LANGUAGE = "ru"

interface WeatherApiService {

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