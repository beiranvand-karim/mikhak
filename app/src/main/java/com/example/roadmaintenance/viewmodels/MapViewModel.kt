package com.example.roadmaintenance.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roadmaintenance.BuildConfig
import com.example.roadmaintenance.api.EndPoints
import com.example.roadmaintenance.api.ServiceBuilder
import com.example.roadmaintenance.models.Pathway
import com.example.roadmaintenance.models.RouteShape
import com.example.roadmaintenance.services.RouteRequestMapper
import com.example.roadmaintenance.services.RouteResponseMapper
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {

    private var routeShapesList  = emptyList<RouteShape>().toMutableList()
    private val _pathCoordinates = MutableStateFlow<List<RouteShape>?>(null)
    val pathCoordinates = _pathCoordinates.asStateFlow()

    private val Tag = "Map View Model"

    fun getRoutesData(pathwayList: List<Pathway>) {
        viewModelScope.launch {
            pathwayList.onEachIndexed { index, pathway ->
                routeApi(pathway)
                if (index == pathwayList.size - 1){
                    _pathCoordinates.emit(routeShapesList)
                }
            }
        }
    }

    private suspend fun routeApi(path: Pathway) {
        try {
            val request = ServiceBuilder.buildPathPointsService(EndPoints::class.java);

            val requestBody = RouteRequestMapper.createRequestBody(path)

            val response = request.getPathInfo(BuildConfig.MAPQUEST_API_TOKEN, requestBody)

            val routeResponseMapper = RouteResponseMapper(
                path.pathId,
                response,
                LatLng(path.latitude_1, path.longitude_1),
                LatLng(path.latitude_2, path.longitude_2)
            )

            routeResponseMapper.routeShapeParcer()?.let {
                routeShapesList.add(it)
            }

        } catch (e: Exception) {
            Log.e(Tag,"an error occurred")
            e.printStackTrace()
        }
    }
}