package com.vctapps.cloud_weatherview.presentation.hardware

import com.google.android.things.pio.Gpio
import com.google.android.things.pio.PeripheralManager
import com.vctapps.cloud_weatherview.domain.WeatherState
import java.util.*

class WeatherLedView {

    private val gpios = listOf("BCM5", "BCM6", "BCM12", "BCM16", "BCM26")

    private val leds = mutableListOf<Gpio>()

    private var timeToBlink = 1000L

    private val random = Random()

    private var ledsBlinking = false

    private val blinkLeds = Runnable {
        while (true) {
            if (ledsBlinking) {
                val position = random.nextInt(5)
                leds[position].on()
                Thread.sleep(300)
                leds[position].off()
                Thread.sleep(100)
                leds[position].on()
                Thread.sleep(100)
                leds[position].off()
            }

            Thread.sleep(timeToBlink)
        }
    }

    private val threadLeds = Thread(blinkLeds)

    fun onStart() {
        for (position in 0..4) {
            val led = PeripheralManager.getInstance().openGpio(gpios[position])
            led.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW)

            leds.add(led)
        }

        threadLeds.start()
    }

    fun onStop() {
        leds.forEach { led -> led.close() }
    }

    fun onChangeState(state: WeatherState) {
        ledsBlinking = false
        disableAll()

        when (state) {
            WeatherState.CLEAR -> showClearSky()
            WeatherState.CLOUDS -> showClouds()
            WeatherState.FEW_CLOUDS -> showFewClouds()
            WeatherState.RAIN -> showRain()
            WeatherState.THUNDERSTORM -> showThunderstorm()
            WeatherState.ERROR -> showErrorLed()
        }
    }

    private fun disableAll() {
        leds.forEach { led -> led.off() }
    }

    private fun showErrorLed() {
        ledsBlinking = false
        //red led on
    }

    private fun showClearSky() {
        ledsBlinking = false
    }

    private fun showFewClouds() {
        ledsBlinking = false
        leds.forEach { led -> led.on() }
    }

    private fun showClouds() {
        ledsBlinking = false
        leds[0].on()
        leds[1].off()
        leds[2].off()
        leds[3].off()
        leds[4].on()
    }

    private fun showRain() {
        putLedsBlinking(1000)
    }

    private fun showThunderstorm() {
        putLedsBlinking(500)
    }

    private fun putLedsBlinking(timeToBlink: Long) {
        ledsBlinking = true
        this.timeToBlink = timeToBlink
    }

}