package com.example.roadmaintenance.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.roadmaintenance.api.EndPoints
import com.example.roadmaintenance.api.ServiceBuilder
import com.example.roadmaintenance.models.Pathway
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class SharedViewModel : ViewModel() {


    private val _sendDataResponse = MutableLiveData<Response<ResponseBody>>()
    var sendResponse = _sendDataResponse

    private val _pathways = MutableLiveData<Response<List<Pathway>>>()
    var pathways = _pathways

    fun getPathways() {
        val request = ServiceBuilder.buildLightPostService(EndPoints::class.java)
        val call = request.getPathways()

        call.enqueue(object : Callback<List<Pathway>> {
            override fun onResponse(call: Call<List<Pathway>>, response: Response<List<Pathway>>) {
                if (response.isSuccessful) {
                    println("fetch data")
                    response?.let {
                        _pathways.postValue(it)
                    }
                } else {
                    Log.e("Fetch data", "Fetch Response is not successful")
                }
            }

            override fun onFailure(call: Call<List<Pathway>>, t: Throwable) {
                Log.e("Fetch data", "${t.message}")
                Log.e("Fetch data", "Fetch Request is not successful")
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
                    getPathways()
                } else {
                    Log.e("Send-Data", response.headers().toString())
                    Log.e("Send-Data", "upload file response is not success full")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Send-Data", "upload file request is not correct")
            }
        })
    }

}