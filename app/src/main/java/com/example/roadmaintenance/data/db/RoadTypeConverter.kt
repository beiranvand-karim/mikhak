package com.example.roadmaintenance.data.db

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng

class RoadTypeConverter {

    @TypeConverter
    fun latLngsToString(segments: List<LatLng>): String {
        return segments.map {
            Pair(it.latitude.toString(), it.longitude.toString())
        }.joinToString(separator = ";") { it.toString() }
    }

    @TypeConverter
    fun stringToLatLngs(str: String): List<LatLng> {
        return if (str.isEmpty())
            emptyList()
        else {
            str.split(";","(",")").map {
//                println(it.split().first().toDouble())
                LatLng(0.0, 0.0)
            }
        }
    }
}