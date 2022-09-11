package com.example.roadmaintenance.road_apis.routeShapeApi

import com.example.roadmaintenance.models.RegisteredRoad
import com.example.roadmaintenance.road_apis.routeShapeApi.request.RoutePathRequest
import com.example.roadmaintenance.road_apis.routeShapeApi.request.RouteRegionRequest
import com.example.roadmaintenance.road_apis.routeShapeApi.response.RoutePathResponseMapper
import com.example.roadmaintenance.road_apis.routeShapeApi.response.RouteRegionResponseMapper
import com.google.gson.JsonObject

interface PathAPIFactory {
    fun createRequest(): RouteRegionRequest
    fun createResponseMapper(baseObject: JsonObject): RouteRegionResponseMapper
}

class RouteRegionApisFactory(private val road: RegisteredRoad) : PathAPIFactory {
    override fun createRequest(): RouteRegionRequest {
        return RouteRegionRequest(road)
    }

    override fun createResponseMapper(baseObject: JsonObject): RouteRegionResponseMapper {
        return RouteRegionResponseMapper(road, baseObject)
    }
}

class RouteShapeApiFactory(private val road: RegisteredRoad) : PathAPIFactory {
    override fun createRequest(): RouteRegionRequest {
        return RoutePathRequest(road)
    }

    override fun createResponseMapper(baseObject: JsonObject): RouteRegionResponseMapper {
        return RoutePathResponseMapper(road, baseObject)
    }
}