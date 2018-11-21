package com.vctapps.cloud_weatherview.di

import com.google.gson.Gson
import com.vctapps.cloud_weatherview.data.forecast.openWeather.OpenWeatherApi
import com.vctapps.cloud_weatherview.data.forecast.openWeather.OpenWeatherDataSource
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single { OpenWeatherDataSource(api = get()) }

    single { createOpenWeatherApi(get()) }

    single {
        Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create(Gson()))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(get())
                .baseUrl("https://api.openweathermap.org/")
                .build()
    }

    single {
        OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
                .build()
    }

}

fun createOpenWeatherApi(retrofit: Retrofit): OpenWeatherApi = retrofit.create(OpenWeatherApi::class.java)