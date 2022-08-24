package com.example.roadmaintenance.road_apis.routeShapeApi.request

import com.example.roadmaintenance.models.RegisteredRoad
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.RequestBody

class AdvanceRequest(private val road: RegisteredRoad) : BasicRequest(road) {

    override fun createRequestBody(): RequestBody {
        return super.createRequestBody()
    }

    override fun createLocations(): JsonArray {
        return super.createLocations()
    }

    override fun createOptions(): JsonObject {
        val options = super.createOptions()
        options.apply {
            add("avoids", JsonArray())
            addProperty("avoidTimedConditions", false)
            addProperty("doReverseGeocode", true)
            addProperty("shapeFormat", "raw")
            addProperty("generalize", 0)
            addProperty("routeType", "fastest")
            addProperty("timeType", 1)
            addProperty("locale", "en_US")
            addProperty("unit", "k")
            addProperty("enhancedNarrative", false)
            addProperty("drivingStyle", 2)
            addProperty("highwayEfficiency", 21.0)
        }
        return options
    }
}