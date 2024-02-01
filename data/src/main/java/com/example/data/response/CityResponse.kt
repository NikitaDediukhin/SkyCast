package com.example.data.response

import com.google.gson.annotations.SerializedName

data class CityResponse(
    @SerializedName("name") val name: String,
    @SerializedName("region") val region: String,
    @SerializedName("country") val country: String,
    @SerializedName("localtime") val localtime: String
)