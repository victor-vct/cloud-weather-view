package com.vctapps.cloud_weatherview.data.state

import com.vctapps.cloud_weatherview.domain.WeatherState
import io.reactivex.Flowable

interface StateDataSource {

    fun state(): Flowable<WeatherState>

}