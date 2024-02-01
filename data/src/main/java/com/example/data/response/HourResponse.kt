package com.example.data.response

import com.google.gson.annotations.SerializedName

data class HourResponse(
    @SerializedName("time") val time: String,
    @SerializedName("temp_c") val avgTemp: Double,
    @SerializedName("condition") val condition: ConditionResponse
)