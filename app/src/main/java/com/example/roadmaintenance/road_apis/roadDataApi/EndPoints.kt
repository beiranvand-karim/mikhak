package com.example.roadmaintenance.road_apis.roadDataApi

import com.example.roadmaintenance.models.RegisteredRoad
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.http.*

interface EndPoints {

    @GET("all_roads")
    suspend fun getRegisteredRoads(): List<RegisteredRoad>

    @Multipart
    @POST("upload_file")
    suspend fun uploadFile(@Part part: MultipartBody.Part): ResponseBody

    @POST("route")
    suspend fun getRoadData(
        @Query("key") key: String,
        @Body requestBody: RequestBody
    ): JsonObject

    @POST("register_road")
    suspend fun registerRoad(
        @Body requestBody: RequestBody
    )

    @POST("submit_light_post")
    suspend fun submitLightPost(
        @Body requestBody: RequestBody
    )
}