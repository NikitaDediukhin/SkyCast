package com.example.data.response

import com.google.gson.annotations.SerializedName

data class ConditionResponse(
    @SerializedName("text") val text: String,
    @SerializedName("icon") val icon: String
)