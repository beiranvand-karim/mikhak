package com.example.roadmaintenance.data.db;

import androidx.room.TypeConverter
import com.example.roadmaintenance.models.CustomPoint

class Converters {

    @TypeConverter
    fun fromPoints(value: List<CustomPoint>?): String? = value?.let {
        var pointsToStr = ""
        it.forEach {
            pointsToStr += "$it;"
        }
        pointsToStr
    }

    @TypeConverter
    fun toPoints(str: String?): List<CustomPoint>? = str?.let { str ->
        val pointList = arrayListOf<CustomPoint>()
        val points = str.split(";")
        points.forEach {
            if (it.isNotEmpty())
                pointList.add(CustomPoint(it))
        }
        pointList
    }
}
