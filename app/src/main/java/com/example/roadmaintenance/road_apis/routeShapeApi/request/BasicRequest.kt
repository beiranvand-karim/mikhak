package com.example.roadmaintenance.road_apis.routeShapeApi.request

import com.example.roadmaintenance.models.RegisteredRoad
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.MediaType
import okhttp3.RequestBody

open class BasicRequest(private val road: RegisteredRoad) {

    open fun createRequestBody() : RequestBody {
        val baseObject = JsonObject()
        baseObject.add("locations", createLocations())
        baseObject.add("options", createOptions())
        return RequestBody.create(MediaType.parse("application/json"), baseObject.toString())
    }

    protected open fun createLocations() : JsonArray{
        val locations = JsonArray()
        val firstLocation = JsonObject()
        val firstLatLng = JsonObject()

        firstLatLng.addProperty("lat", road.latitude_1)
        firstLatLng.addProperty("lng", road.longitude_1)

        firstLocation.add("latLng", firstLatLng)

        locations.add(firstLocation)

        val secondLocation = JsonObject()
        val secondLatLng = JsonObject()

        secondLatLng.addProperty("lat", road.latitude_2)
        secondLatLng.addProperty("lng", road.longitude_2)

        secondLocation.add("latLng", secondLatLng)

        locations.add(secondLocation)
        return locations
    }

    protected open fun createOptions() : JsonObject = JsonObject()


}