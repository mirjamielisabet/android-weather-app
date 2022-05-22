package fi.tuni.weatherapp

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

/**
 * Represents the weather forecast.
 *
 * @property description the description of the weather
 * @property icon the identifier of the weather icon
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class Forecast(var description: String, var icon: String)

/**
 * Represents the forecast results from the API call.
 *
 * @property list list of the ForecastList objects
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class ForecastResult(var list: MutableList<ForecastList>)

/**
 * Represents a single weather forecast result from the list of forecast results.
 *
 * @property main ForecastTemperature object
 * @property weather list of information about the weather
 * @property wind ForecastWind object
 * @property dt_txt the date of the weather forecast
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class ForecastList(var main: ForecastTemperature, var weather: MutableList<Forecast>,
                        var wind: ForecastWind, var dt_txt: String)

/**
 * Represents the wind.
 *
 * @property speed the speed of the wind
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class ForecastWind(var speed: Double)

/**
 * Represents the temperature.
 *
 * @property temp the outside temperature
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class ForecastTemperature(var temp: Double)
