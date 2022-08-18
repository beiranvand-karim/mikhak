package com.example.roadmaintenance.data.repository

import com.example.roadmaintenance.BuildConfig
import com.example.roadmaintenance.api.EndPoints
import com.example.roadmaintenance.api.ServiceBuilder
import com.example.roadmaintenance.models.RegisteredRoad
import com.example.roadmaintenance.services.RoadRequestCreator
import com.example.roadmaintenance.services.RoadResponseCreator
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class RoadRepository {

    private val roadService = ServiceBuilder.buildRegisteredRoadsService(EndPoints::class.java)

    suspend fun refreshRoads() {
        withContext(Dispatchers.IO) {
            val registeredRoads = roadService.getRegisteredRoads()

            registeredRoads.forEach {
                val road = getRoadsData(it)
                // TODO: insert data
            }
        }
    }

    private suspend fun getRoadsData(road: RegisteredRoad): RegisteredRoad {
        return withContext(Dispatchers.IO) {
            val roadDataService = ServiceBuilder.buildRoadsDataService(EndPoints::class.java)
            val requestBody = RoadRequestCreator.createRequestBody(road)

            val response = roadDataService.getRoadData(BuildConfig.MAPQUEST_API_TOKEN, requestBody)

            val routeResponseMapper = RoadResponseCreator(
                road.roadId,
                response,
                LatLng(road.latitude_1, road.longitude_1),
                LatLng(road.latitude_2, road.longitude_2)
            )

            routeResponseMapper.routeShapeParcer()?.apply {
                road.roadData = this
            }
            road
        }
    }

    suspend fun uploadData(file: File) {
        withContext(Dispatchers.IO) {
            val requestBody = RequestBody.create(null, file)
            val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
            roadService.uploadFile(part)
        }
        refreshRoads()
    }

}