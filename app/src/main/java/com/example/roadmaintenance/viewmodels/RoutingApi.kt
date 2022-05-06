package com.example.roadmaintenance.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.roadmaintenance.BuildConfig
import com.example.roadmaintenance.api.EndPoints
import com.example.roadmaintenance.api.ServiceBuilder
import com.example.roadmaintenance.models.Pathway
import com.example.roadmaintenance.services.RouteRequestMapper
import com.example.roadmaintenance.services.RouteResponseMapper
import com.google.android.gms.maps.model.LatLng
import com.google.gson.JsonObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RoutingApi : ViewModel() {

    private val TAG = "Path Points Api"

    private val _latlngList = MutableLiveData<List<LatLng>>()
    var latLngList = _latlngList

    fun fetchPoints(path: Pathway) {
        val request = ServiceBuilder.buildPathPointsService(EndPoints::class.java);

        val requestBody = RouteRequestMapper.createRequestBody(path)

        val call = request.getPathPoints(BuildConfig.GRAPHHOPPER_API_TOKEN, requestBody)

        call.enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if (response.isSuccessful) {
                    Log.i(TAG, "Response is successful")
                    response?.let {
                        Log.i(TAG, it.body().toString())
                        val latLngList = RouteResponseMapper.jsonObject(it?.body())
                        latLngList.add(0,LatLng(path.latitude_1,path.longitude_1))
                        latLngList.add(LatLng(path.latitude_2,path.longitude_2))
                        latLngList?.also {
                            _latlngList.postValue(latLngList)
                        }
                    }
                } else {
                    Log.e(TAG, "Response is not successful!!!")
                    Log.e(TAG, response.message())
                    Log.e(TAG, response.body().toString())
                    Log.e(TAG, response.code().toString())
                    Log.e(TAG, response.raw().toString())
                }
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                Log.e(TAG, "Request is not successful!!!")
            }

        })

    }
}