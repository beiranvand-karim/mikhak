package com.example.roadmaintenance.road_apis.roadDataApi

import com.example.roadmaintenance.BuildConfig
import com.example.roadmaintenance.models.RegisteredRoad
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object BackendServiceBuilder {
    val gson = GsonBuilder()
        .setDateFormat("HH:mm:ss")
        .setDateFormat("yyyy-mm-dd")
        .create()

    private var serverUrl: String? = BuildConfig.SERVER_URL

    private val retrofitRegisteredRoad = Retrofit.Builder()
        .baseUrl(serverUrl)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .build()

    fun <T> buildRegisteredRoadsService(service: Class<T>): T {
        return retrofitRegisteredRoad.create(service)
    }
}