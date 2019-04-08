package com.vctapps.cloud_weatherview.domain

data class WeatherForecast(val temperature: Float = 0f,
                           val weather: WeatherState = WeatherState.ERROR)

