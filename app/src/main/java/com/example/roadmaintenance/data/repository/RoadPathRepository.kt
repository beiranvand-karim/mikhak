package com.example.roadmaintenance.data.repository

import com.example.roadmaintenance.BuildConfig
import com.example.roadmaintenance.models.RegisteredRoad
import com.example.roadmaintenance.models.RoadPath
import com.example.roadmaintenance.road_apis.roadDataApi.EndPoints
import com.example.roadmaintenance.road_apis.roadDataApi.RoadDataServiceBuilder
import com.example.roadmaintenance.road_apis.routeShapeApi.PathAPIFactory
import com.example.roadmaintenance.road_apis.routeShapeApi.RouteShapeApiFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext

class RoadPathRepository {

    lateinit var pathApiFactory: PathAPIFactory

    val roadsPathFlow: MutableSharedFlow<RoadPath> = MutableSharedFlow()

    suspend fun getRoadsPathSegments(roads: List<RegisteredRoad>) {
        roads.forEach {
            pathApiFactory = RouteShapeApiFactory(it)
            withContext(Dispatchers.IO) {
                val roadPath = getRoadsPathWay()
                roadPath
                    .takeUnless { roadPath ->
                        roadPath == null || roadPath.segments.isNullOrEmpty()
                    }
                    ?.apply {
                        roadsPathFlow.emit(this)
                    }
            }
        }
    }

    suspend fun getRoadsPathWay(): RoadPath? {
        var roadPath: RoadPath? = null
        return withContext(Dispatchers.IO) {
            val roadDataService = RoadDataServiceBuilder.buildRoadsDataService(EndPoints::class.java)

            val requestBody = pathApiFactory.createRequest().createRequestBody()

            val response =
                roadDataService.getRoadData(BuildConfig.MAPQUEST_API_TOKEN, requestBody)

            val routeResponseMapper =
                pathApiFactory.createResponseMapper(response)

            routeResponseMapper.routeShapeParcer()?.apply {
                roadPath = this
            }
            roadPath
        }
    }
}
