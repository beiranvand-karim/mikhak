package com.example.roadmaintenance.services

import android.util.Log
import com.google.gson.JsonObject
import com.google.android.gms.maps.model.LatLng
import com.example.roadmaintenance.services.RouteResponseMapper
import com.google.android.gms.maps.model.LatLngBounds
import com.google.gson.JsonParseException
import com.google.gson.JsonArray
import java.lang.Exception
import java.util.ArrayList
import kotlin.math.max
import kotlin.math.min

class RouteResponseMapper(
    private val baseObject: JsonObject,
    private val firstLocation: LatLng,
    private val secondLocation: LatLng
) {

    private var minLat = min(firstLocation.latitude, secondLocation.latitude)
    private val minLng = min(firstLocation.longitude, secondLocation.longitude)
    private val maxLat = max(firstLocation.latitude, secondLocation.latitude)
    private val maxLng = max(firstLocation.longitude, secondLocation.longitude)

    private val Tag = "Route Response Mapper"

    fun coordinatesList(): MutableList<LatLng>? {
        return try {
            val pointsList = baseObject["route"]
                .asJsonObject["shape"]
                .asJsonObject["shapePoints"]
                .asJsonArray
            parseJsonCoordinates(pointsList)
        } catch (e: JsonParseException) {
            Log.e(Tag, e.message!!)
            null
        }
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

}