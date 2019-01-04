package com.vctapps.cloud_weatherview

import android.app.Activity
import android.os.Bundle
import android.util.Log
import com.vctapps.cloud_weatherview.data.forecast.openWeather.OpenWeatherDataSource
import com.vctapps.cloud_weatherview.data.state.StateDataSource
import com.vctapps.cloud_weatherview.di.networkModule
import com.vctapps.cloud_weatherview.domain.WeatherForecast
import com.vctapps.cloud_weatherview.domain.WeatherState
import com.vctapps.cloud_weatherview.presentation.hardware.WeatherLedView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.weather_forecast.*
import org.koin.android.ext.android.inject
import org.koin.android.ext.android.startKoin

class MainActivity : Activity() {

    private val openWeatherDataSource: OpenWeatherDataSource by inject()
    private val stateDataSource: StateDataSource by inject()

    private val weatherLeds = WeatherLedView()

    private val disposable = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.weather_forecast)

        startKoin(this, listOf(networkModule))

        weatherLeds.onStart()

        disposable.add(
                openWeatherDataSource
                        .forecast()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ response ->
                            Log.d("Forecast", response.toString())
                            showInScreen(response)
                            showInHardware(response.weather)
                        }, { error ->
                            Log.e("Forecast", error.message)
                            showError()
                        })
        )

        disposable.add(
                stateDataSource
                        .state()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({ response ->
                            Log.d("State", response.toString())
                            showInHardware(response)
                        }, { error ->
                            Log.e("State", error.message)
                            showError()
                        })
        )
    }

    private fun showInHardware(state: WeatherState) {
        weatherLeds.onChangeState(state)
    }

    private fun showInScreen(response: WeatherForecast) {
        textWeatherTemp.text = "${response.temperature}ºC"
        textWeatherForecast.text = getWeatherText(response.weather)
    }

    private fun getWeatherText(response: WeatherState): String {
        return when (response) {
            WeatherState.CLEAR -> getString(R.string.clear)
            WeatherState.FEW_CLOUDS -> getString(R.string.few_clouds)
            WeatherState.CLOUDS -> getString(R.string.clouds)
            WeatherState.RAIN -> getString(R.string.rain)
            WeatherState.THUNDERSTORM -> getString(R.string.thunderstorm)
            else -> getString(R.string.error_message)
        }
    }

    private fun showError() {
        textWeatherForecast.text = getString(R.string.error_message)
    }

    override fun onDestroy() {
        weatherLeds.onStop()
        disposable.dispose()
        super.onDestroy()
    }
}
