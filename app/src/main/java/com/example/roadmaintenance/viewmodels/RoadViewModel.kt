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
import kotlinx.coroutines.launch
import java.io.File

class RoadViewModel(application: Application) : AndroidViewModel(application) {

    private val roadDataBase = RoadDataBase.getDatabase(application.applicationContext)
    private val roadRepository = RegisteredRoadRepository(roadDataBase)
    private val roadPathRepository = RoadPathRepository()
    private val tag = "RoadViewModel"
    val roads = roadRepository.getAllRoads.asLiveData()
    val roadPathFlow = roadPathRepository.roadsPathFlow.asLiveData()

    fun refreshRoads() {
        if (NetworkConnection.IsInternetAvailable) {
            viewModelScope.launch {
                try {
                    roadRepository.refreshRoads()
                } catch (e: Exception) {
                    Log.e("$tag refresh data", e.stackTraceToString())
                }
            }
        }
    }

    fun uploadFile(file: File) {
        viewModelScope.launch {
            try {
                roadRepository.uploadData(file)
            } catch (e: Exception) {
                Log.e("$tag Upload File", e.stackTraceToString())
            }
        }
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
}