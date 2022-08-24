package com.example.roadmaintenance.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.roadmaintenance.data.db.RoadDataBase
import com.example.roadmaintenance.data.repository.RoadRepository
import com.example.roadmaintenance.network.NetworkConnection
import kotlinx.coroutines.launch
import java.io.File

class RoadViewModel(application: Application) : AndroidViewModel(application) {

    private val roadDataBase = RoadDataBase.getDatabase(application.applicationContext)
    private val roadRepository = RoadRepository(roadDataBase)

    val roads = roadRepository.getAllRoads.asLiveData()

    private val tag = "ROAD VIEW MODEL"

    fun refreshRoads() {
        if(NetworkConnection.IsInternetAvailable) {
            viewModelScope.launch {
                try {
                    roadRepository.refreshRoads()
                } catch (e: Exception) {
                    Log.e("$tag Refresh Data", e.stackTraceToString())
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
}