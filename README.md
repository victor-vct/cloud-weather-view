# Cloud weather view

<p align="center">
  <img src="assets/cloud.gif" align="center" width=250>
</p>

## The Cloud

This project simulate a cloud with leds and cotton to show forecast weather. When boot the system, the cloud connect to an API and get current forecast and show with LEDs to simulating this states:

1. Thunderstorm. LEDs blinking a lot
2. Rain. LEDs blinking less
3. Clouds. Just 3 LEDs on
4. Few Clouds. All LEDs on
5. Clear (All LEDs off, but I am thinking in a better way to represent this state)

## Hardware

There are two implementations:

1. Raspberry Pi with Android Things
2. NodeMCU ESP8266 with Arduino IDE

## The App

The app just control cloud state using Firebase RealtimeDatabase. Just for tests and showcase.

<p align="center">
  <img src="assets/app_screenshot.jpg" align="center" width=250>
</p>
