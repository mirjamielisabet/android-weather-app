package fi.tuni.weatherapp

import android.app.Activity
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

/**
 * The class that contains the functions needed for a url connection.
 */
class UrlConnection {

    /**
     * Gets the content from the url and directs it to the callback function.
     *
     * @param context the Activity context.
     * @param url the url of the content to be downloaded.
     * @param callback the callback function that takes the result as a parameter.
     */
    fun downloadUrlAsync(context: Activity, url: String, callback: (result: String?) -> Unit) {
        thread() {
            val json = getUrl(url)
            context.runOnUiThread() {
                callback(json)
            }
        }
    }

    /**
     * By using HttpUrlConnection and BufferedReader, the content is read and saved to
     * the result variable. The result is returned if no errors occur.
     *
     * @param url the url of the content to be read.
     * @return the result if the url was read successfully and null if there was an exception.
     */
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
