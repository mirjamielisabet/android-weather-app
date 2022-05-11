package fi.tuni.weatherapp

import com.fasterxml.jackson.annotation.JsonIgnoreProperties

@JsonIgnoreProperties(ignoreUnknown = true)
data class Forecast(var description: String, var icon: String)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ForecastResult(var list: MutableList<ForecastList>)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ForecastList(var main: ForecastTemperature, var weather: MutableList<Forecast>,
                        var wind: ForecastWind, var dt_txt: String)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ForecastWind(var speed: Double)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ForecastTemperature(var temp: Double)