package com.vctapps.cloud_weatherview

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.vctapps.cloud_weatherview.data.forecast.openWeather.OpenWeatherDataSource
import com.vctapps.cloud_weatherview.di.networkModule
import com.vctapps.cloud_weatherview.domain.WeatherState
import com.vctapps.cloud_weatherview.presentation.hardware.WeatherLedView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.weather_forecast.*
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.startKoin

class MainActivity : Activity() {

    private val openWeatherDataSource: OpenWeatherDataSource by inject()

    private val weatherLeds = WeatherLedView()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather_forecast)

        startKoin(this, listOf(networkModule))

        weatherLeds.onStart()

        openWeatherDataSource
                .forecast()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ response ->
                    Log.d("State", response.toString())
                    textWeatherForecast.text = "${response.temperature}ºC"
                    weatherLeds.onChangeState(response.weather)
                }, { error ->
                    Log.e("State", error.message)
                    textWeatherForecast.text = "ERROR :("
                })
    }

    override fun onDestroy() {
        weatherLeds.onStop()
        super.onDestroy()
    }
}
