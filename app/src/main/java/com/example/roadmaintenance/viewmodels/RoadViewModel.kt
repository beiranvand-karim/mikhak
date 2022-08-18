package com.example.roadmaintenance.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.roadmaintenance.data.repository.RoadRepository
import kotlinx.coroutines.launch
import java.io.File

class RoadViewModel(application: Application) : AndroidViewModel(application) {

    private val roadRepository = RoadRepository()
    private final val tag = "ROAD VIEW MODEL"

    fun refreshRoads() {
        viewModelScope.launch {
            try {
                roadRepository.refreshRoads()
            } catch (e: Exception) {
                Log.e(tag,e.message!!)
            }
        }
    }

    fun uploadFile(file: File) {
        viewModelScope.launch {
            try {
                roadRepository.uploadData(file)
            } catch (e: Exception) {
                Log.e(tag,e.message!!)
            }
        }
    }

}