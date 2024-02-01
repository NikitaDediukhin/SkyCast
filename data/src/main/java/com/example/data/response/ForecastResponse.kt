package com.example.data.response

import com.google.gson.annotations.SerializedName

data class ForecastResponse(
    @SerializedName("forecastday") val forecastday: List<ForecastDayResponse>
)