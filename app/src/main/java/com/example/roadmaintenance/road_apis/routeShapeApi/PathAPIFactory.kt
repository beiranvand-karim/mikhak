package com.example.roadmaintenance.road_apis.routeShapeApi

import com.example.roadmaintenance.models.RegisteredRoad
import com.example.roadmaintenance.road_apis.routeShapeApi.request.AdvanceRequest
import com.example.roadmaintenance.road_apis.routeShapeApi.request.BasicRequest
import com.example.roadmaintenance.road_apis.routeShapeApi.response.AdvanceResponseMapperMapper
import com.example.roadmaintenance.road_apis.routeShapeApi.response.BasicResponseMapper
import com.google.gson.JsonObject

interface PathAPIFactory {

    fun createRequest(): BasicRequest

    fun createResponseMapper(baseObject: JsonObject): BasicResponseMapper
}

class BasicApisFactory(private val road: RegisteredRoad) : PathAPIFactory {
    override fun createRequest(): BasicRequest {
        return BasicRequest(road)
    }

    override fun createResponseMapper(baseObject: JsonObject): BasicResponseMapper {
        return BasicResponseMapper(road, baseObject)
    }
}

class AdvanceApisFactory(private val road: RegisteredRoad) : PathAPIFactory {
    override fun createRequest(): BasicRequest {
        return AdvanceRequest(road)
    }

    override fun createResponseMapper(baseObject: JsonObject): BasicResponseMapper {
        return AdvanceResponseMapperMapper(road, baseObject)
    }
}