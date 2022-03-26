package com.example.roadmaintenance.processor

import com.example.roadmaintenance.models.LightPost
import com.example.roadmaintenance.models.LightPostSideEnum
import com.example.roadmaintenance.models.Road
import java.io.InputStream

class CSVProcessor(private var file: InputStream?) {

    fun readCsv(): Road {
        var values: List<String> =
            file?.bufferedReader()?.useLines { lines -> lines.last().split(",") }!!
        var location = values[0]
        var width = values[1].toInt()
        var distanceEachLightPost = values[2].toInt()
        var lightPostHeight = values[3].toInt()
        var power = values[4].toInt()
        var lightProductionType = values[5]

        var electricalPostSides: LightPostSideEnum =
            if (values[6].equals("one_side", ignoreCase = true)) LightPostSideEnum.ONE_SIDE
            else LightPostSideEnum.TWO_SIDES


        var road = Road(
            location, width, distanceEachLightPost, LightPost(
                0, lightPostHeight, power,
                lightProductionType, electricalPostSides
            )
        )
        return road
    }

}