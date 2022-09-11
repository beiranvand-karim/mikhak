package com.example.roadmaintenance.road_apis.routeShapeApi.response

import com.example.roadmaintenance.models.RegisteredRoad
import com.example.roadmaintenance.models.RoadPath
import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonArray
import com.google.gson.JsonObject

open class RouteRegionResponseMapper(
    protected val road: RegisteredRoad,
    protected val baseObject: JsonObject,
) {
    protected val id: Double = road.pathId
    protected val firstLocation: LatLng = LatLng(road.latitude_1, road.longitude_1)
    protected val secondLocation: LatLng = LatLng(road.latitude_2, road.longitude_2)

    open fun routeShapeParcer(): RoadPath? {
        val regions =
            try {
                val routeObject = baseObject["route"].asJsonObject
                extractRouteRegions(routeObject)
            } catch (e: Exception) {
                null
            }
        return RoadPath(id, regions)
    }

    protected open fun extractRouteRegions(routeObject: JsonObject): String? {
        val regions = arrayListOf<String>()
        return try {
            routeObject.getAsJsonArray("locations").forEach {
                regions.add(it.asJsonObject.get("adminArea5").asString)
            }
            regions.joinToString(separator = " _ ", prefix = "", postfix = "")
        } catch (e: Exception) {
            null
        }
    }

    protected open fun parseJsonCoordinates(coordinates: JsonArray): MutableList<LatLng>? = null
}