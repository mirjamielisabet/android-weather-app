package fi.tuni.weatherapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Geocoder
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    lateinit var heading : TextView
    lateinit var heading2 : TextView
    lateinit var locationText : TextView
    lateinit var description : TextView
    lateinit var temp : TextView
    lateinit var feelsLikeTemp : TextView
    lateinit var wind : TextView
    lateinit var humidity : TextView
    lateinit var submitButton : Button
    lateinit var locationInput : EditText
    lateinit var weatherIcon : ImageView
    lateinit var fusedLocationClient: FusedLocationProviderClient
    lateinit var key : String
    lateinit var sharedPref : SharedPreferences
    lateinit var searchedLocation : String

    private var errmsg = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        key = getString(R.string.key)
        heading = findViewById(R.id.heading)
        heading2 = findViewById(R.id.heading2)
        locationText = findViewById(R.id.locationText)
        submitButton = findViewById(R.id.submitButton)
        locationInput = findViewById(R.id.locationInput)
        description = findViewById(R.id.description)
        temp = findViewById(R.id.temp)
        feelsLikeTemp = findViewById(R.id.feels_like_temp)
        wind = findViewById(R.id.wind)
        humidity = findViewById(R.id.humidity)
        weatherIcon = findViewById(R.id.weatherIcon)

        heading.text = getString(R.string.currentWeather)
        heading2.text = getString(R.string.searchLocation)
        searchedLocation = getString(R.string.defaultCity)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        sharedPref = getSharedPreferences("savedLocation", MODE_PRIVATE)
    }

    fun getWeatherData() {
        val url = "https://api.openweathermap.org/data/2.5/weather?q=${searchedLocation}&units=metric&appid=${key}"
        UrlConnection().downloadUrlAsync(this, url) {
            if (it != null) {
                val result = parseWeatherJSON(it)
                if (result != null) {
                    locationInput.text = null
                    locationInput.clearFocus()

                    temp.text = getString(R.string.temp, result.main.temp)
                    feelsLikeTemp.text = getString(R.string.feels_like_temp, result.main.feels_like)
                    wind.text = getString(R.string.wind, result.wind.speed)
                    humidity.text = getString(R.string.humidity, result.main.humidity)
                    description.text = result.weather[0].description
                    locationText.text = result.name
                    val edit = sharedPref.edit()
                    edit.putString("location", result.name)
                    edit.apply()
                    Glide.with(this)
                        .load("https://openweathermap.org/img/w/${result.weather[0].icon}.png")
                        .into(weatherIcon)
                } else {
                    heading.text = errmsg
                    heading.setTextColor(Color.parseColor("red"))
                }
            } else {
                errmsg = "Entered Location Not Found"
                locationInput.error = errmsg
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val savedLocation = sharedPref.getString("location", "")
        if (savedLocation != "" && savedLocation != null) {
            searchedLocation = savedLocation
        }
        getWeatherData()
    }

    fun onClick(button: View) {
        val intent = Intent(this, ForecastActivity::class.java)
        intent.putExtra("location", locationText.text)

        when (button.id) {
            R.id.submitButton -> {
                val inputText = locationInput.text.toString()
                searchedLocation = inputText
                getWeatherData()
            }
            R.id.forecastButton -> startActivity(intent)
            R.id.currentLocationButton -> getCurrentLocation()
        }
    }

    fun askPermissions() {
        ActivityCompat.requestPermissions(this,
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), 42)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            42 -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    getCurrentLocation()
                } else {
                    Toast.makeText(this,
                        "Access to Location denied, cannot show Weather Info based on your location.",
                        Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    fun checkPermission() : Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation() {
        if (checkPermission()) {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { currentLocation : Location? ->
                    if (currentLocation != null) {
                        val city = getCity(currentLocation.latitude, currentLocation.longitude)
                        searchedLocation = city
                        getWeatherData()
                    }
                }
        } else {
            askPermissions()
        }
    }

    fun getCity(lat: Double, lon: Double) : String {
        val geocoder = Geocoder(this)
        val list = geocoder.getFromLocation(lat, lon, 1)
        return list[0].locality
    }

    fun parseWeatherJSON(json: String): WeatherResult? {
        return try {
            val mapper = ObjectMapper().registerKotlinModule()
            mapper.readValue(json, WeatherResult::class.java)
        } catch (e: Exception) {
            errmsg = "Error When Processing the Weather Results"
            null
        }
    }
}