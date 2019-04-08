package com.vctapps.cloud_weatherview.data.forecast

import com.vctapps.cloud_weatherview.data.BaseOpenWeatherNetworkTest
import com.vctapps.cloud_weatherview.data.forecast.openWeather.OpenWeatherDataSource
import com.vctapps.cloud_weatherview.domain.WeatherState
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mockito.*

class ForecastDataSourceTest : BaseOpenWeatherNetworkTest() {

    private lateinit var dataSource: ForecastDataSource

    @Before
    override fun setUp() {
        super.setUp()

        dataSource = OpenWeatherDataSource(openWeatherApi)
    }

    @Test
    fun `should return error state when api key is wrong`() {
        setFailureResponse("weather_invalid_key.json", 401)

        val forecast = dataSource.forecast()
                .test()
                .assertNoErrors()
                .values()[0]

        assertEquals(0f, forecast.temperature)
        assertEquals(WeatherState.ERROR, forecast.weather)
    }

    @Test
    fun `should return error state when geocode is wrong`() {
        setFailureResponse("weather_invalid_geocode.json", 400)

        val forecast = dataSource.forecast()
                .test()
                .assertNoErrors()
                .values()[0]

        assertEquals(0f, forecast.temperature)
        assertEquals(WeatherState.ERROR, forecast.weather)
    }

    @Test
    fun `should return error state when account is blocked`() {
        setFailureResponse("weather_account_failed.json", 429)

        val forecast = dataSource.forecast()
                .test()
                .assertNoErrors()
                .values()[0]

        assertEquals(0f, forecast.temperature)
        assertEquals(WeatherState.ERROR, forecast.weather)
    }

    @Test
    fun `should request weather with clear weather`() {
        setSuccessResponse("weather_success_clear_sky.json")

        val forecast = dataSource.forecast()
                .test()
                .assertNoErrors()
                .values()[0]

        assertEquals(19.67f, forecast.temperature)
        assertEquals(WeatherState.CLEAR, forecast.weather)
    }

    @Test
    fun `should request weather with cloud weather from broken cloud response`() {
        setSuccessResponse("weather_success_broken_cloud.json")

        val forecast = dataSource.forecast()
                .test()
                .assertNoErrors()
                .values()[0]

        assertEquals(19.67f, forecast.temperature)
        assertEquals(WeatherState.CLOUDS, forecast.weather)
    }

    @Test
    fun `should request weather with cloud weather from scattered cloud response`() {
        setSuccessResponse("weather_success_scattered_cloud.json")

        val forecast = dataSource.forecast()
                .test()
                .assertNoErrors()
                .values()[0]

        assertEquals(19.67f, forecast.temperature)
        assertEquals(WeatherState.CLOUDS, forecast.weather)
    }

    @Test
    fun `should request weather with few cloud weather`() {
        setSuccessResponse("weather_success_few_cloud.json")

        val forecast = dataSource.forecast()
                .test()
                .assertNoErrors()
                .values()[0]

        assertEquals(19.67f, forecast.temperature)
        assertEquals(WeatherState.FEW_CLOUDS, forecast.weather)
    }

    @Test
    fun `should request weather with rain weather from drizzle response`() {
        setSuccessResponse("weather_success_drizzle.json")

        val forecast = dataSource.forecast()
                .test()
                .assertNoErrors()
                .values()[0]

        assertEquals(19.67f, forecast.temperature)
        assertEquals(WeatherState.RAIN, forecast.weather)
    }

    @Test
    fun `should request weather with rain weather`() {
        setSuccessResponse("weather_success_rain.json")

        val forecast = dataSource.forecast()
                .test()
                .assertNoErrors()
                .values()[0]

        assertEquals(19.67f, forecast.temperature)
        assertEquals(WeatherState.RAIN, forecast.weather)
    }

    @Test
    fun `should request weather with thunderstorm weather`() {
        setSuccessResponse("weather_success_thunderstorm.json")

        val forecast = dataSource.forecast()
                .test()
                .assertNoErrors()
                .values()[0]

        assertEquals(19.67f, forecast.temperature)
        assertEquals(WeatherState.THUNDERSTORM, forecast.weather)
    }

}