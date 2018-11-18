package com.vctapps.cloud_weatherview.data

import com.squareup.okhttp.mockwebserver.MockResponse
import com.squareup.okhttp.mockwebserver.MockWebServer
import com.vctapps.cloud_weatherview.data.forecast.openWeather.OpenWeatherApi
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Before
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

open class BaseOpenWeatherNetworkTest {

    val mockServer = MockWebServer()

    lateinit var openWeatherApi: OpenWeatherApi

    @Before
    open fun setUp() {
        val okHttpClient = OkHttpClient.Builder()
                .build()

        val retrofit = Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .baseUrl(mockServer.url("/").toString())
                .build()

        openWeatherApi = retrofit.create(OpenWeatherApi::class.java)
    }

    @After
    fun teardown(){
        mockServer.shutdown()
    }

    fun setSuccessResponse(jsonFileName: String) {
        val mockedResponse = MockResponse()
        mockedResponse.setResponseCode(200)
        mockedResponse.setBody(JsonLoader.getStringJson(jsonFileName))

        mockServer.enqueue(mockedResponse)
    }

    fun setFailureResponse(jsonFileName: String, errorCode: Int){
        val mockedResponse = MockResponse()
        mockedResponse.setResponseCode(errorCode)
        mockedResponse.setBody(JsonLoader.getStringJson(jsonFileName))

        mockServer.enqueue(mockedResponse)
    }

}