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

class RoutePathResponseMapper(
    road: RegisteredRoad,
    baseObject: JsonObject
) : RouteRegionResponseMapper(road, baseObject) {

    private var minLat = min(firstLocation.latitude, secondLocation.latitude)
    private val minLng = min(firstLocation.longitude, secondLocation.longitude)
    private val maxLat = max(firstLocation.latitude, secondLocation.latitude)
    private val maxLng = max(firstLocation.longitude, secondLocation.longitude)

    private val Tag = "Route Advance Response Mapper"

    override fun routeShapeParcer(): RoadPath? {
        val roadPath = road.roadPath
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

    override fun parseJsonCoordinates(coordinates: JsonArray): MutableList<LatLng>? {
        return try {
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
            latLngList
        } catch (e: Exception) {
            Log.e(Tag, e.message.toString())
            null
        }
    }

    private fun checkLatLng(lat: Double, lng: Double): Boolean {
        return (lat > minLat &&
                lat < maxLat) ||
                (lng > minLng &&
                        lng < maxLng)
    }
}