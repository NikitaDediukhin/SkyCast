package com.example.data.response

import com.google.gson.annotations.SerializedName

data class ForecastDayResponse(
    @SerializedName("date") val date: String,
    @SerializedName("day") val dayWeather: DayResponse,
    @SerializedName("hour") val hourWeather: List<HourResponse>
)