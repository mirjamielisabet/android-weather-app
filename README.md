# Weather App

Android Mobile Application that shows the current weather and the forecast for the user's chosen location.

The user can check the weather and forecast for the current location by using the device's location sensors or by entering the location in the input field.
The current weather section shows the basic information about the weather: temperature, feels-like temperature, humidity, wind speed, weather description and weather icon. The forecast is for the next five days (with a 3-hour step), and shows a bit shorter info about the weather: date, weather icon, temperature, wind speed and weather description.

When the App is used for the first time, the default location is Helsinki. The App remembers the last location searched, so when it restarts, it shows the weather based on that location.

This App is created as a part of the Native Mobile Development course in TAMK (Tampere University of Applied Sciences).

## Author

Mirjami Laiho

## Screenshots

<img width="300" alt="screenshot-1" src="https://user-images.githubusercontent.com/77788900/169082019-a50627a8-64a7-49e9-94b4-e50742f3bed5.jpg"> &nbsp;&nbsp;&nbsp; <img width="300" alt="screenshot-2" src="https://user-images.githubusercontent.com/77788900/169082959-8c3e6449-345c-4555-88c2-ab8c9579c92d.jpg"> &nbsp;&nbsp;&nbsp; <img width="300" alt="screenshot-3" src="https://user-images.githubusercontent.com/77788900/169083029-835cff05-41da-4bef-b527-f1677e1654f8.jpg">

## Technologies / Built with

- Kotlin
- Android Studio
- [OpenWeather API](https://openweathermap.org/api)
- [Material Design Components for Android](https://material.io/components?platform=android)
- [Glide](https://github.com/bumptech/glide)
- [Jackson](https://github.com/FasterXML/jackson)

## How to use

1. Open [Android Studio](https://developer.android.com/studio?gclid=Cj0KCQjwvqeUBhCBARIsAOdt45Zoph2Yy7bwV5HxzKvAh4S5LtfxYiG_pmeAQz0PPKTJBw0AbJwFiogaAvKaEALw_wcB&gclsrc=aw.ds) and select <b>File</b> > <b>New</b> > <b>Project from Version Control</b>
2. URL:  ```https://github.com/mirjamielisabet/android-weather-app```  and the Directory of your choise, then click Clone. Login to Github if needed.
3. Create an account and get your own (free) API key from here:  [OpenWeather - Sign Up](https://home.openweathermap.org/users/sign_up)
4. In the cloned project's res/values folder, there is a file <i>strings.xml</i>. Open it.
5. Add line ```<string name="key">INSERT YOUR API KEY HERE</string>```  into that strings.xml file and replace the <i>INSERT YOUR API KEY HERE</i> text with your API key.
6. Run the App using Android phone or emulator.


## Screencast

[Screencast - Youtube](https://youtu.be/pBPSh1c-XbI)

