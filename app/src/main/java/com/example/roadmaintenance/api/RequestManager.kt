package com.example.roadmaintenance.api

import android.content.Context
import android.util.Log
import com.example.roadmaintenance.fileManager.FileCache
import com.example.roadmaintenance.models.Path
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class RequestManager {

    private var isUploadDataSuccess: Boolean = false
    private var pathList: List<Path>? = null

    fun uploadData(file: File): Boolean {
        val request = ServiceBuilder.buildService(EndPoints::class.java)

        val requestBody = RequestBody.create(null, file)
        val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
        val call = request.uploadFile(part)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("Send-Data", "response is successful")
                    isUploadDataSuccess = true
                } else {
                    Log.e("Send-Data", response.headers().toString())
                    Log.e("Send-Data", "upload file response is not success full")
                    isUploadDataSuccess = false
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Send-Data", "upload file request is not correct")
                isUploadDataSuccess = false
            }
        })
        return isUploadDataSuccess
    }

    fun fetchData(): List<Path>? {
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getUsers()

        call.enqueue(object : Callback<List<Path>> {
            override fun onResponse(call: Call<List<Path>>, response: Response<List<Path>>) {
                if (response.isSuccessful) {
                    println("fetch data")
                    pathList = response.body()
                } else {
                    Log.e("Fetch data", "Fetch Response is not successful")
                }
            }

            override fun onFailure(call: Call<List<Path>>, t: Throwable) {
                Log.e("Fetch data", "${t.message}")
                Log.e("Fetch data", "Fetch Request is not successful")
            }
        })
        return pathList
    }

}