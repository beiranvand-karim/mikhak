package com.example.roadmaintenance.services

import android.util.Log
import com.example.roadmaintenance.models.RouteShape
import com.google.gson.JsonObject
import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonParseException
import com.google.gson.JsonArray
import java.lang.Exception
import kotlin.math.max
import kotlin.math.min

class RouteResponseMapper(
    private val id: Double,
    private val baseObject: JsonObject,
    private val firstLocation: LatLng,
    private val secondLocation: LatLng
) {

    private var minLat = min(firstLocation.latitude, secondLocation.latitude)
    private val minLng = min(firstLocation.longitude, secondLocation.longitude)
    private val maxLat = max(firstLocation.latitude, secondLocation.latitude)
    private val maxLng = max(firstLocation.longitude, secondLocation.longitude)

    private val Tag = "Route Response Mapper"

    fun routeShapeParcer(): RouteShape? {

        val segments = try {
            val pointsList = baseObject["route"]
                .asJsonObject["shape"]
                .asJsonObject["shapePoints"]
                .asJsonArray
            parseJsonCoordinates(pointsList)
        } catch (e: JsonParseException) {
            Log.e(Tag, e.message!!)
            null
        }

        val locations =
            try {
                val routeObject = baseObject["route"].asJsonObject
                extractRouteLocations(routeObject)
            } catch (e: Exception) {
                Log.e(Tag, e.message!!)
                null
            }

        if (!segments.isNullOrEmpty() && !locations.isNullOrEmpty())
            return RouteShape(id, locations, segments.toList())
        return null
    }

    private fun parseJsonCoordinates(coordinates: JsonArray): MutableList<LatLng>? {
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

            Log.i(Tag, latLngList.toString())
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

    private fun extractRouteLocations(routeObject: JsonObject): ArrayList<String>? {
        val locations = arrayListOf<String>()

        return try {
            routeObject.getAsJsonArray("locations").forEach {
                locations.add(it.asJsonObject.get("adminArea5").asString)
            }
            println(locations)
            locations
        } catch (e: Exception) {
            null
        }
    }
}