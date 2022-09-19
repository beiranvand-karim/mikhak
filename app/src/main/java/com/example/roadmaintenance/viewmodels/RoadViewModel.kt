package com.example.roadmaintenance.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.roadmaintenance.data.db.RoadDataBase
import com.example.roadmaintenance.data.repository.RegisteredRoadRepository
import com.example.roadmaintenance.data.repository.RoadPathRepository
import com.example.roadmaintenance.models.RegisteredRoad
import com.example.roadmaintenance.network.NetworkConnection
import com.example.roadmaintenance.util.*
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import java.io.File

class RoadViewModel(application: Application) : AndroidViewModel(application) {

    private val roadDataBase = RoadDataBase.getDatabase(application.applicationContext)
    private val roadRepository = RegisteredRoadRepository(roadDataBase)
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
                    resultState.emit(ServerErrorResultsCreator(e.localizedMessage!!).resultFactory())
                }
            }
        } else {
            returnOfflineError()
        }
    }

    suspend fun returnSuccessRequest() = resultState.emit(SuccessResultsCreator.resultFactory())

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

    fun getLightPostsByRoadId(id: Double) = roadRepository.getLightPostsByRoadId(id).asLiveData()

    fun getRoadPathSegment(roads: List<RegisteredRoad>) {
        viewModelScope.launch {
            try {
                roadPathRepository.getRoadsPathSegments(roads)
            } catch (e: Exception) {
                Log.e("$tag get roads segments", e.stackTraceToString())
            }
        }
    }

    fun submitLightState(road: RegisteredRoad) {
        viewModelScope.launch {
            returnLoadingState()
            try {
                roadRepository.submitLightState(road)
                refreshRoads()
                returnSuccessRequest()
            } catch (e: Exception) {
                Log.e("$tag submit light state", e.stackTraceToString())
                resultState.emit(ServerErrorResultsCreator(e.localizedMessage).resultFactory())
            }
        }
    }
}