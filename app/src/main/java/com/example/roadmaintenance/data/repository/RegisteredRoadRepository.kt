package com.example.roadmaintenance.data.repository

import com.example.roadmaintenance.data.db.RoadDataBase
import com.example.roadmaintenance.models.LightPost
import com.example.roadmaintenance.models.RegisteredRoad
import com.example.roadmaintenance.road_apis.roadDataApi.EndPoints
import com.example.roadmaintenance.road_apis.roadDataApi.ServiceBuilder
import com.example.roadmaintenance.road_apis.routeShapeApi.RouteRegionApisFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

class RegisteredRoadRepository(private val roadDataBase: RoadDataBase) {

    private val roadService = ServiceBuilder.buildRegisteredRoadsService(EndPoints::class.java)
    private val roadDao = roadDataBase.roadDao()
    private val roadPathRepository: RoadPathRepository by lazy { RoadPathRepository() }
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
                        insertLightPosts(road.lightPosts!!)
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
            roadPathRepository.pathApiFactory = RouteRegionApisFactory(it)
            it.roadPath = roadPathRepository.getRoadsPathWay()
        }
        roadWithDetails.forEach {
            it.lightPostCounts = it.lightPosts?.size!!
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

    fun getLightPostsByRoadId(id: Double) =
        roadDao.getAllLightPostsByRoadId(id)
}