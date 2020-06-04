package com.bdn.collinsceleb.capturefarm.models

import androidx.room.TypeConverter
import com.google.gson.Gson


class LocationConverter {
    @TypeConverter
    fun toLocation(locationDouble: Double?): Location? {
        return try {
            Gson().fromJson(locationDouble.toString(), Location::class.java)
        } catch (e: Exception) {
            null
        }
    }

    @TypeConverter
    fun toLocationString(location: Location?): Double? {
        return Gson().toJson(location).toDouble()
    }
}