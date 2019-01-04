package com.vctapps.cloud_weatherview.data.forecast.openWeather.response

import com.google.gson.annotations.SerializedName

data class OpenWeatherResponse(
        @SerializedName("id") val weatherId: Int?,
        @SerializedName("main") val weather: String?,
        @SerializedName("description") val description: String?
)