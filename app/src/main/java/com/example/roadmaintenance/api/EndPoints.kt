package com.example.roadmaintenance.api

import com.example.roadmaintenance.models.Pathway
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface EndPoints {

    @GET("paths")
    fun getUsers(): Call<List<Pathway>>

    @Multipart
    @POST("upload_file")
    fun uploadFile(@Part part: MultipartBody.Part): Call<ResponseBody>

}