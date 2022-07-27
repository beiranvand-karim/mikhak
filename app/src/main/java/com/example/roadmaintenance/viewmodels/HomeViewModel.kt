package com.example.roadmaintenance.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roadmaintenance.BuildConfig
import com.example.roadmaintenance.api.EndPoints
import com.example.roadmaintenance.api.ServiceBuilder
import com.example.roadmaintenance.models.Pathway
import com.example.roadmaintenance.services.RouteRequestMapper
import com.example.roadmaintenance.services.RouteResponseMapper
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private var pathwayList = emptyList<Pathway>().toMutableList()
    private val _shapedPath = MutableSharedFlow<List<Pathway>?>()
    val shapedPath = _shapedPath.asSharedFlow()

    private val Tag = "Map View Model"

    fun getRoutesData(pathwayList: List<Pathway>) {
        viewModelScope.launch {
            pathwayList.onEachIndexed { index, pathway ->
                routeApi(pathway)
                if (index == pathwayList.size - 1) {
                    _shapedPath.emit(pathwayList)
                }
            }
        }
    }

    private suspend fun routeApi(path: Pathway) {
        try {

            val shapedPath: Pathway = path

            val request = ServiceBuilder.buildPathPointsService(EndPoints::class.java)

            val requestBody = RouteRequestMapper.createRequestBody(path)

            val response = request.getPathInfo(BuildConfig.MAPQUEST_API_TOKEN, requestBody)

            val routeResponseMapper = RouteResponseMapper(
                path.pathId,
                response,
                LatLng(path.latitude_1, path.longitude_1),
                LatLng(path.latitude_2, path.longitude_2)
            )

            routeResponseMapper.routeShapeParcer()?.let {
                shapedPath.routeShape = it
                pathwayList.add(shapedPath)
            }

        } catch (e: Exception) {
            Log.e(Tag, "an error occurred")
            e.printStackTrace()
        }
    }
}