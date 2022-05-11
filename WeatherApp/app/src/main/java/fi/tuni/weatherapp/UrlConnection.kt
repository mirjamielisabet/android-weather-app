package fi.tuni.weatherapp

import android.app.Activity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

class UrlConnection {
    fun downloadUrlAsync(context: Activity, url: String, callback: (result: String?) -> Unit) {
        thread() {
            val json = getUrl(url)
            context.runOnUiThread() {
                callback(json)
            }
        }
    }

    fun getUrl(url: String) : String? {
        var result : String? = ""

        try {
            val myUrl = URL(url)
            val conn = myUrl.openConnection() as HttpURLConnection
            val reader = BufferedReader(InputStreamReader(conn.inputStream))

            reader.use {
                var line : String? = ""

                while (line != null) {
                    line = it.readLine()
                    result += line
                }
            }
        } catch (e: Exception) {
            result = null
        }
        return result
    }
}