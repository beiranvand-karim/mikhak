package com.example.roadmaintenance.api

import com.example.roadmaintenance.models.RegisteredRoad
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface EndPoints {

    @GET("paths")
    suspend fun getRegisteredRoads(): List<RegisteredRoad>

    @Multipart
    @POST("upload_file")
    suspend fun uploadFile(@Part part: MultipartBody.Part): ResponseBody

    @POST("route")
    suspend fun getRoadData(
        @Query("key") key: String,
        @Body requestBody: RequestBody
    ): JsonObject
}