package com.vctapps.cloud_weatherview.data.forecast.openWeather

import com.vctapps.cloud_weatherview.BuildConfig
import com.vctapps.cloud_weatherview.data.forecast.ForecastDataSource
import com.vctapps.cloud_weatherview.domain.WeatherForecast
import com.vctapps.cloud_weatherview.domain.WeatherState
import io.reactivex.Single

class OpenWeatherDataSource(private val api: OpenWeatherApi) : ForecastDataSource {

    override fun forecast(): Single<WeatherForecast> {
        return api.getWeather(
                BuildConfig.OPEN_WEATHER_API_KEY,
                BuildConfig.OPEN_WEATHER_LAT,
                BuildConfig.OPEN_WEATHER_LONG,
                "metric",
                "pt")
                .flatMap { response ->
                    if(response.isSuccessful) {
                        Single.just(getWeatherForecast(response.body()))
                    } else {
                        Single.just(WeatherForecast())
                    }
                }
    }

    private fun getWeatherForecast(response: OpenWeatherRootResponse?): WeatherForecast {
        val temperature = response?.now?.temperature ?: 0f
        val weatherId = response?.forecasts?.get(0)?.weatherId ?: 0

        val state = when (weatherId) {
            800 -> WeatherState.CLEAR
            801 -> WeatherState.FEW_CLOUDS
            in 200..232 -> WeatherState.THUNDERSTORM
            in 300..321 -> WeatherState.RAIN
            in 500..531 -> WeatherState.RAIN
            in 802..804 -> WeatherState.CLOUDS
            else -> WeatherState.ERROR
        }

        return WeatherForecast(temperature, state)
    }

}