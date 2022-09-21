package com.example.roadmaintenance.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.roadmaintenance.data.db.RoadDataBase
import com.example.roadmaintenance.data.repository.OfflineModeRepository
import com.example.roadmaintenance.data.repository.RegisteredRoadRepository
import com.example.roadmaintenance.data.repository.RoadPathRepository
import com.example.roadmaintenance.models.LightPost
import com.example.roadmaintenance.models.RegisteredRoad
import com.example.roadmaintenance.network.NetworkConnection
import com.example.roadmaintenance.util.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.io.File

class RoadViewModel(application: Application) : AndroidViewModel(application) {

    private val roadDataBase = RoadDataBase.getDatabase(application.applicationContext)
    private val roadRepository = RegisteredRoadRepository(roadDataBase)
    private val offlineRepository = OfflineModeRepository(roadDataBase)
    private val roadPathRepository = RoadPathRepository()
    private val tag = "RoadViewModel"
    val roads = roadRepository.getAllRoads.asLiveData()
    val roadPathFlow = roadPathRepository.roadsPathFlow.asLiveData()
    val resultState: MutableSharedFlow<Results> = MutableSharedFlow()

    fun refreshRoads() {
        if (NetworkConnection.IsInternetAvailable) {
            viewModelScope.launch {
                returnLoadingState()
                try {
                    roadRepository.refreshData()
                    returnSuccessRequest()
                } catch (e: Exception) {
                    Log.e("$tag refresh data", e.stackTraceToString())
                    returnServerError(e)
                }
            }
        } else {
            returnOfflineError()
        }
    }

    private suspend fun returnServerError(e: Exception) {
        resultState.emit(ServerErrorResultsCreator(e.localizedMessage!!).resultFactory())
    }

    private suspend fun returnSuccessRequest() =
        resultState.emit(SuccessResultsCreator.resultFactory())

    fun uploadFile(file: File) {
        if (NetworkConnection.IsInternetAvailable) {
            viewModelScope.launch {
                returnLoadingState()
                try {
                    roadRepository.uploadData(file)
                    resultState.emit(UploadFileSuccessCreator.resultFactory())
                    refreshRoads()
                } catch (e: Exception) {
                    Log.e("$tag Upload File", e.stackTraceToString())
                    resultState.emit(UploadFileErrorCreator.resultFactory())
                }
            }
        } else {
            returnOfflineError()
        }
    }

    fun returnOfflineError() {
        val offlineResults = OfflineResultsCreator.resultFactory()
        viewModelScope.launch {
            resultState.emit(offlineResults)
        }
    }

    private suspend fun returnLoadingState() {
        resultState.emit(LoadingResultsCreator.resultFactory())
    }

    fun getLightPostsByRoadIdAsFlow(id: Double) =
        roadRepository.getLightPostsByRoadIdAsFlows(id).asLiveData()

    fun getRoadPathSegment(roads: List<RegisteredRoad>) {
        viewModelScope.launch {
            try {
                roadPathRepository.getRoadsPathSegments(roads)
            } catch (e: Exception) {
                Log.e("$tag get roads segments", e.stackTraceToString())
            }
        }
    }

    fun registerEntireLightState(road: RegisteredRoad) {
        if (NetworkConnection.IsInternetAvailable) {
            viewModelScope.launch {
                returnLoadingState()
                try {
                    roadRepository.registerEntireLightState(road)
                    refreshRoads()
                    returnSuccessRequest()
                } catch (e: Exception) {
                    Log.e("$tag submit light state", e.stackTraceToString())
                    resultState.emit(ServerErrorResultsCreator(e.localizedMessage).resultFactory())
                    submitLightStateInOfflineMode(road)
                }
            }
        } else {
            viewModelScope.launch {
                submitLightStateInOfflineMode(road)
            }
        }
    }

    private fun submitLightStateInOfflineMode(road: RegisteredRoad) {
        viewModelScope.launch {
            if (!roadRepository.isRoadExists(road.roadId)) {
                road.isSyncWithServer = 0
                offlineRepository.saveInOfflineMode(road)
            } else {
                road.lightPosts?.map {
                    it.isSyncWithServer = 0
                }
                road.lightPosts?.let {
                    offlineRepository.saveLightPostsInCache(it)
                }
            }
        }
    }

    suspend fun getNotSyncedRoads() =
        offlineRepository.getNotSyncedRoads()

    suspend fun getNotSyncedLightPosts(): List<LightPost> =
        offlineRepository.getNotSyncedLightPosts()

    fun syncRoadsWithServer(roads: List<RegisteredRoad>) {
        viewModelScope.launch {
            try {
                roads.forEach {
                    launch {
                        try {
                            val lpList = getLightPostsByRoadIdAsList(it.roadId)
                            it.lightPosts = lpList
                            roadRepository.registerEntireLightState(it)
                            clearRoadSyncFlag(it.roadId)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("$tag sync data with server", e.stackTraceToString())
            }
        }
    }

    private suspend fun getLightPostsByRoadIdAsList(id: Double) =
        offlineRepository.getLightPostsByIdAsList(id)

    fun syncLightPostWithServer(lpList: List<LightPost>) {
        viewModelScope.launch {
            try {
                lpList.forEach {
                    try {
                        roadRepository.submitLightPost(it)
                        clearLightPostSyncFlag(it.registeredRoad.roadId)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e: Exception) {
                Log.e("$tag sync data with server", e.stackTraceToString())
            }
        }
    }

    private suspend fun clearRoadSyncFlag(roadId: Double) {
        offlineRepository.clearRoadSyncFlag(roadId)
    }

    private suspend fun clearLightPostSyncFlag(roadId: Double) {
        offlineRepository.clearLightPostSyncFlag(roadId)
    }
}