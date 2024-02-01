package com.example.data.response

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("location") val location: CityResponse,
    @SerializedName("forecast") val forecast: ForecastResponse,
    @SerializedName("condition") val condition: String
)