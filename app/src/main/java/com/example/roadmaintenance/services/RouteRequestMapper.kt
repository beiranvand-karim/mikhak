package com.example.roadmaintenance.services

import android.util.Log
import com.example.roadmaintenance.models.Pathway
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.RequestBody

object RouteRequestMapper {
    private const val Tag = "Route Request Mapper"
    fun createRequestBody(path: Pathway): RequestBody {
        val baseObject = JsonObject()
        baseObject.add("locations", createLocations(path))
        baseObject.add("options", createOptions())
        Log.i(Tag, baseObject.toString())
        return RequestBody.create(MediaType.parse("application/json"), baseObject.toString())
    }

    private fun createLocations(path: Pathway): JsonArray {
        val locations = JsonArray()
        val firstLocation = JsonObject()
        val firstLatLng = JsonObject()
        firstLatLng.addProperty("lat", path.latitude_1)
        firstLatLng.addProperty("lng", path.longitude_1)
        firstLocation.add("latLng", firstLatLng)
        locations.add(firstLocation)
        val secondLocation = JsonObject()
        val secondLatLng = JsonObject()
        secondLatLng.addProperty("lat", path.latitude_2)
        secondLatLng.addProperty("lng", path.longitude_2)
        secondLocation.add("latLng", secondLatLng)
        locations.add(secondLocation)
        return locations
    }

    private fun createOptions(): JsonObject {
        val options = JsonObject()
        options.add("avoids", JsonArray())
        options.addProperty("avoidTimedConditions", false)
        options.addProperty("doReverseGeocode", true)
        options.addProperty("shapeFormat", "raw")
        options.addProperty("generalize", 0)
        options.addProperty("routeType", "fastest")
        options.addProperty("timeType", 1)
        options.addProperty("locale", "en_US")
        options.addProperty("unit", "m")
        options.addProperty("enhancedNarrative", false)
        options.addProperty("drivingStyle", 2)
        options.addProperty("highwayEfficiency", 21.0)
        return options
    }
}