package fi.tuni.weatherapp

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Weather(var description: String, var icon: String)

@JsonIgnoreProperties(ignoreUnknown = true)
data class WeatherResult(var weather: MutableList<Weather>, var main: Temperature, var wind: Wind)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Temperature(var temp: Double, var feels_like: Double, var humidity: Int)

@JsonIgnoreProperties(ignoreUnknown = true)
data class Wind(var speed: Double)