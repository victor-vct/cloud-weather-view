package com.vctapps.cloud_weatherview.presentation.hardware

import com.google.android.things.pio.Gpio

fun Gpio.on() {
    value = true
}

fun Gpio.off() {
    value = false
}