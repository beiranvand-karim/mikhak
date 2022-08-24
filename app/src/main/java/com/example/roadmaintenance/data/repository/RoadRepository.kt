package com.example.roadmaintenance.data.repository

import com.example.roadmaintenance.BuildConfig
import com.example.roadmaintenance.data.db.RoadDataBase
import com.example.roadmaintenance.models.LightPost
import com.example.roadmaintenance.models.RegisteredRoad
import com.example.roadmaintenance.models.RoadPath
import com.example.roadmaintenance.road_apis.routeShapeApi.BasicApisFactory
import com.example.roadmaintenance.road_apis.routeShapeApi.PathAPIFactory
import com.example.roadmaintenance.road_apis.roadDataApi.EndPoints
import com.example.roadmaintenance.road_apis.roadDataApi.ServiceBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class RoadRepository(private val roadDataBase: RoadDataBase) {

    private val roadService = ServiceBuilder.buildRegisteredRoadsService(EndPoints::class.java)

    private val roadDao = roadDataBase.roadDao()

    private lateinit var pathFactory: PathAPIFactory

    val getAllRoads = roadDao.getAllRoads()

    suspend fun refreshRoads() {
        withContext(Dispatchers.IO) {
            val registeredRoads = roadService.getRegisteredRoads()
            registeredRoads.takeUnless {
                it.isNullOrEmpty()
            }?.apply {
                launch {
                    insertRoads(this@apply)
                }
                launch {
                    registeredRoads.forEach { road ->
                        insertLightPosts(road._lightPosts)
                    }
                }
            }
        }
    }

    private suspend fun insertRoads(roads: List<RegisteredRoad>) {
        val roadModelsToSave = setRoadDetailsBeforeSaving(roads)
        roadDao.insertRoads(roadModelsToSave)
    }

    private suspend fun setRoadDetailsBeforeSaving(roads: List<RegisteredRoad>): List<RegisteredRoad> {
        val roadWithDetails = roads
        roadWithDetails.map {
            pathFactory = BasicApisFactory(it)
            it.roadPath = getRoadsPathWay()
        }
        roadWithDetails.forEach {
            it.lightPostCounts = it._lightPosts.size
        }
        return roadWithDetails
    }

    private suspend fun insertLightPosts(lightPosts: List<LightPost>) {
        roadDao.insertLightPosts(lightPosts)
    }


    suspend fun uploadData(file: File) {
        withContext(Dispatchers.IO) {
            val requestBody = RequestBody.create(null, file)
            val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
            roadService.uploadFile(part)
        }
        refreshRoads()
    }

    private suspend fun getRoadsPathWay(): RoadPath? {
        var roadPath: RoadPath? = null
        return withContext(Dispatchers.IO) {
            val roadDataService = ServiceBuilder.buildRoadsDataService(EndPoints::class.java)

            val requestBody = pathFactory.createRequest().createRequestBody()

            val response =
                roadDataService.getRoadData(BuildConfig.MAPQUEST_API_TOKEN, requestBody)

            val routeResponseMapper =
                pathFactory.createResponseMapper(response)

            routeResponseMapper.routeShapeParcer()?.apply {
                roadPath = this
            }
            roadPath
        }
    }

}