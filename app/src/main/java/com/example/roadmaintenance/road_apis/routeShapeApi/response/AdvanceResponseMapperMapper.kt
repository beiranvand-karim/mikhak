package com.example.roadmaintenance.road_apis.routeShapeApi.response

import android.util.Log
import com.example.roadmaintenance.models.RegisteredRoad
import com.example.roadmaintenance.models.RoadPath
import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonParseException
import kotlin.math.max
import kotlin.math.min

class AdvanceResponseMapperMapper(
    road: RegisteredRoad,
    baseObject: JsonObject
) : BasicResponseMapper(road, baseObject) {

    private var minLat = min(firstLocation.latitude, secondLocation.latitude)
    private val minLng = min(firstLocation.longitude, secondLocation.longitude)
    private val maxLat = max(firstLocation.latitude, secondLocation.latitude)
    private val maxLng = max(firstLocation.longitude, secondLocation.longitude)

    private val Tag = "Route Advance Response Mapper"

    override fun routeShapeParcer(): RoadPath? {
        val roadPath = super.routeShapeParcer()

        val segments = try {
            val pointsList = baseObject["route"]
                .asJsonObject["shape"]
                .asJsonObject["shapePoints"]
                .asJsonArray
            parseJsonCoordinates(pointsList)
        } catch (e: JsonParseException) {
            null
        }
        roadPath?.segments = segments
        return roadPath
    }

    override fun extractRouteRegions(routeObject: JsonObject): String? =
        super.extractRouteRegions(routeObject)

    override fun parseJsonCoordinates(coordinates: JsonArray): MutableList<LatLng>? {
        try {
            val latLngList: MutableList<LatLng> = ArrayList()
            var i = 0
            while (i < coordinates.size()) {
                if (checkLatLng(coordinates[i].asDouble, coordinates[i + 1].asDouble)) {
                    latLngList.add(
                        LatLng(
                            coordinates[i].asDouble,
                            coordinates[i + 1].asDouble
                        )
                    )
                }
                i += 2
            }
            return latLngList
        } catch (e: Exception) {
            Log.e(Tag, e.message.toString())
            return null
        }
    }

    private fun checkLatLng(lat: Double, lng: Double): Boolean {
        if ((lat > minLat &&
                    lat < maxLat) ||
            (lng > minLng &&
                    lng < maxLng)
        ) {
            return true
        }
        return false
    }

}