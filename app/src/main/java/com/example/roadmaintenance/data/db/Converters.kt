package com.example.roadmaintenance.data.db

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
    fun toPoints(str: String?): List<CustomPoint>? = str?.let {
        val pointList = arrayListOf<CustomPoint>()
        val points = it.split(";")
        points.forEach { p ->
            if (p.isNotEmpty())
                pointList.add(CustomPoint(p))
        }
        pointList
    }
}
