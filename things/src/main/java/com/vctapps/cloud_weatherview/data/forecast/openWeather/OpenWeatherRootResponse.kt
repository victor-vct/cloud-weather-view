package com.vctapps.cloud_weatherview.data.forecast.openWeather

import com.google.gson.annotations.SerializedName

data class OpenWeatherRootResponse(
        @SerializedName("weather") val forecasts: List<OpenWeatherResponse>?,
        @SerializedName("main") val now: OpenWeatherMainResponse?
)