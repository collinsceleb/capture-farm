package com.bdn.collinsceleb.capturefarm.models

import android.util.Log
import com.bdn.collinsceleb.capturefarm.R
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.*

class PlaceApi {
    fun autoComplete(input: String): ArrayList<String> {
        val arrayList = ArrayList<String>()
        var connection: HttpURLConnection? = null
        val jsonResult = StringBuilder()
        try {
            val sb =
                "https://maps.googleapis.com/maps/api/place/autocomplete/json?" + "input=" + input +
                        R.string.google_maps_key
            val url = URL(sb)
            connection = url.openConnection() as HttpURLConnection
            val inputStreamReader =
                InputStreamReader(connection.inputStream)
            var read: Int
            val buff = CharArray(1024)
            while (inputStreamReader.read(buff).also { read = it } != -1) {
                jsonResult.append(buff, 0, read)
            }
            Log.d("JSon", jsonResult.toString())
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            connection?.disconnect()
        }
        try {
            val jsonObject = JSONObject(jsonResult.toString())
            val prediction = jsonObject.getJSONArray("predictions")
            for (i in 0 until prediction.length()) {
                arrayList.add(prediction.getJSONObject(i).getString("description"))
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return arrayList
    }
}