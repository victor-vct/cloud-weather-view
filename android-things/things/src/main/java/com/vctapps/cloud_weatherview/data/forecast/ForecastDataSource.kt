package com.vctapps.cloud_weatherview.data.forecast

import com.vctapps.cloud_weatherview.domain.WeatherForecast
import io.reactivex.Single

interface ForecastDataSource {

    fun forecast(): Single<WeatherForecast>

}