package com.vctapps.cloud_weatherview.data.forecast.openWeather

import com.vctapps.cloud_weatherview.data.forecast.openWeather.response.OpenWeatherRootResponse
import io.reactivex.Single
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenWeatherApi {

    @GET("/data/2.5/weather")
    fun getWeather(@Query("APPID") appId: String,
                   @Query("lat") lat: String,
                   @Query("lon") long: String,
                   @Query("units") units: String,
                   @Query("lang") language: String): Single<Response<OpenWeatherRootResponse>>

}