package com.vctapps.cloud_weatherview.data.forecast.openWeather.response

import com.google.gson.annotations.SerializedName

data class OpenWeatherMainResponse(
        @SerializedName("temp") val temperature: Float?,
        @SerializedName("temp_min") val temperatureMax: Float?,
        @SerializedName("temp_max") val temperatureMin: Float?
)