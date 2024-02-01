package com.example.data.response

import com.google.gson.annotations.SerializedName

data class DayResponse(
    @SerializedName("maxtemp_c") val maxTemp: Double,
    @SerializedName("mintemp_c") val minTemp: Double,
    @SerializedName("avgtemp_c") val avgTemp: Double,
    @SerializedName("condition") val condition: ConditionResponse
)