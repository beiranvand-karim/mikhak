package com.example.roadmaintenance.viewmodels

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.roadmaintenance.api.EndPoints
import com.example.roadmaintenance.api.ServiceBuilder
import com.example.roadmaintenance.models.RegisteredRoad
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class SharedViewModel : ViewModel() {


    private val _isUploadFileSuccess = MutableSharedFlow<Boolean>()
    var isUploadFileSuccess = _isUploadFileSuccess

    private val _registeredRoads = MutableSharedFlow<Response<List<RegisteredRoad>>>()
    var registeredRoads = _registeredRoads

    fun getRegisteredRoads() {
        val request = ServiceBuilder.buildLightPostService(EndPoints::class.java)
        val call = request.getRegisteredRoads()

        call.enqueue(object : Callback<List<RegisteredRoad>> {
            override fun onResponse(call: Call<List<RegisteredRoad>>, response: Response<List<RegisteredRoad>>) {
                if (response.isSuccessful) {
                    println("fetch data")
                    response.let {
                        viewModelScope.launch {
                            _registeredRoads.emit(it)
                        }
                    }
                } else {
                    Log.e("Fetch data", "Fetch Response is not successful")
                }
            }

            override fun onFailure(call: Call<List<RegisteredRoad>>, t: Throwable) {
                Log.e("Fetch data", "Fetch Request is not successful")
                t.printStackTrace()
            }
        })
    }


    fun uploadFile(file: File) {
        val request = ServiceBuilder.buildLightPostService(EndPoints::class.java)

        val requestBody = RequestBody.create(null, file)
        val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
        val call = request.uploadFile(part)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("Send-Data", "response is successful")
                    viewModelScope.launch {
                        isUploadFileSuccess.emit(true)
                    }
                } else {
                    Log.e("Send-Data", response.headers().toString())
                    Log.e("Send-Data", "upload file response is not success full")
                    viewModelScope.launch {
                        isUploadFileSuccess.emit(false)
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Send-Data", "upload file request is not correct")
                t.printStackTrace()
                viewModelScope.launch {
                    isUploadFileSuccess.emit(false)
                }
            }
        })
    }

}