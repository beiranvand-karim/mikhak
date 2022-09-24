package com.example.roadmaintenance.data.repository

import com.example.roadmaintenance.data.db.RoadDataBase
import com.example.roadmaintenance.models.LightPost
import com.example.roadmaintenance.models.RegisteredRoad
import com.example.roadmaintenance.road_apis.roadDataApi.BackendServiceBuilder
import com.example.roadmaintenance.road_apis.roadDataApi.EndPoints
import com.example.roadmaintenance.road_apis.routeShapeApi.RouteRegionApisFactory
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

open class RegisteredRoadRepository(
    private val roadDataBase: RoadDataBase
) {

    private val roadService =
        BackendServiceBuilder.buildRegisteredRoadsService(EndPoints::class.java)
    protected val roadDao = roadDataBase.roadDao()
    private val roadPathRepository: RoadPathRepository by lazy { RoadPathRepository() }
    val getAllRoads = roadDao.getAllRoads()

    suspend fun refreshData() {
        val roads = fetchAllRegisteredRoads()
        val roadsToSave = setRoadsPathsBeforeSave(roads)
        insertRoadsAndLightPosts(roadsToSave)
    }

    private suspend fun fetchAllRegisteredRoads(): List<RegisteredRoad> =
        withContext(Dispatchers.IO) {
            roadService.getRegisteredRoads()
        }

    protected suspend fun insertRoadsAndLightPosts(registeredRoads: List<RegisteredRoad>?) {
        withContext(Dispatchers.IO) {
            registeredRoads?.takeUnless { it.isNullOrEmpty() }?.apply {
                forEach {
                    launch {
                        insertRoad(it)
                    }
                    launch {
                        insertLightPosts(it.lightPosts)
                    }
                }
            }
        }
    }

    private suspend fun insertRoad(road: RegisteredRoad) {
        withContext(Dispatchers.IO) {
            roadDao.insertRoad(road)
        }
    }

    private suspend fun setRoadsPathsBeforeSave(roads: List<RegisteredRoad>): List<RegisteredRoad> {
        val newList = roads
        withContext(Dispatchers.IO) {
            newList.map {
                launch {
                    roadPathRepository.pathApiFactory = RouteRegionApisFactory(it)
                    it.roadPath = roadPathRepository.getRoadsPathWay()
                }
                launch {
                    setRoadLightPostsCountsBeforeSaving(it)
                }
            }
        }
        return newList
    }

    protected fun setRoadLightPostsCountsBeforeSaving(road: RegisteredRoad) {
        road.lightPostCounts = road.lightPosts?.size!!
    }

    protected suspend fun insertLightPosts(lightPosts: List<LightPost>?) {
        lightPosts.takeUnless { it.isNullOrEmpty() }?.apply {
            roadDao.insertLightPosts(this)
        }
    }

    suspend fun uploadData(file: File) {
        withContext(Dispatchers.IO) {
            val requestBody = RequestBody.create(null, file)
            val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
            roadService.uploadFile(part)
        }
        refreshData()
    }

    fun getLightPostsByRoadIdAsFlows(id: Double) =
        roadDao.getAllLightPostsByRoadIdAsFlows(id)

    suspend fun registerEntireLightState(road: RegisteredRoad) {
        withContext(Dispatchers.IO) {
            val request = RequestBody.create(
                okhttp3.MediaType.parse("application/json; charset=utf-8"),
                Gson().toJson(road)
            )
            roadService.registerRoad(request)
        }
    }

    suspend fun submitLightPost(lp: LightPost) {
        withContext(Dispatchers.IO) {
            val request = RequestBody.create(
                okhttp3.MediaType.parse("application/json; charset=utf-8"),
                Gson().toJson(lp)
            )
            roadService.submitLightPost(request)
        }
    }

    suspend fun isRoadExists(id: Double): Boolean =
        roadDao.isRoadExists(id) > 0
}