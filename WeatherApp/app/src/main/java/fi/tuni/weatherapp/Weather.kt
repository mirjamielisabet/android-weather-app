package fi.tuni.weatherapp

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Represents the weather.
 *
 * @property description the description of the weather
 * @property icon the identifier of the weather icon
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Weather(var description: String, var icon: String)

/**
 * Represents the result of the weather data API call.
 *
 * @property weather list of information about the weather
 * @property main Temperature object
 * @property wind Wind object
 * @property name the name of the location
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class WeatherResult(var weather: MutableList<Weather>, var main: Temperature, var wind: Wind, var name: String)

/**
 * Represents the temperature and it's related factors.
 *
 * @property temp the outside temperature
 * @property feels_like the feels-like temperature
 * @property humidity the number describing the humidity of the air
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Temperature(var temp: Double, var feels_like: Double, var humidity: Int)

/**
 * Represents the wind.
 *
 * @property speed the speed of the wind
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Wind(var speed: Double)
