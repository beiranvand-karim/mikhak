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
import kotlinx.coroutines.launch

class MapViewModel : ViewModel() {

    private val _pathCoordinates = MutableSharedFlow<RouteShape>(0)
    var pathCoordinates = _pathCoordinates

    private val Tag = "Map View Model"

    fun getRoutesData(pathway: List<Pathway>) {
        viewModelScope.launch {
            pathway.forEach {
                routeApi(it)
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
                _pathCoordinates.emit(it)
            }

        } catch (e: Exception) {
            Log.e(Tag,"an error occurred")
            e.printStackTrace()
        }
    }
}