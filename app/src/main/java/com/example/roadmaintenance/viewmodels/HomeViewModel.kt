package com.example.roadmaintenance.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roadmaintenance.BuildConfig
import com.example.roadmaintenance.api.EndPoints
import com.example.roadmaintenance.api.ServiceBuilder
import com.example.roadmaintenance.models.RegisteredRoad
import com.example.roadmaintenance.services.RoadRequestCreator
import com.example.roadmaintenance.services.RoadResponseCreator
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private var registeredRoads = emptyList<RegisteredRoad>().toMutableList()
    private val _roadData = MutableSharedFlow<List<RegisteredRoad>?>()
    val roadData = _roadData.asSharedFlow()

    private val Tag = "Map View Model"

    fun getRoutesData(registeredRoadList: List<RegisteredRoad>) {
        viewModelScope.launch {
            registeredRoadList.onEachIndexed { index, pathway ->
                routeApi(pathway)
                if (index == registeredRoadList.size - 1) {
                    _roadData.emit(registeredRoadList)
                }
            }
        }
    }

    private suspend fun routeApi(path: RegisteredRoad) {
        try {

            val shapedPath: RegisteredRoad = path

            val request = ServiceBuilder.buildPathPointsService(EndPoints::class.java)

            val requestBody = RoadRequestCreator.createRequestBody(path)

            val response = request.getRoadData(BuildConfig.MAPQUEST_API_TOKEN, requestBody)

            val routeResponseMapper = RoadResponseCreator(
                path.roadId,
                response,
                LatLng(path.latitude_1, path.longitude_1),
                LatLng(path.latitude_2, path.longitude_2)
            )

            routeResponseMapper.routeShapeParcer()?.let {
                shapedPath.roadData = it
                registeredRoads.add(shapedPath)
            }

        } catch (e: Exception) {
            Log.e(Tag, "an error occurred")
            e.printStackTrace()
        }
    }
}