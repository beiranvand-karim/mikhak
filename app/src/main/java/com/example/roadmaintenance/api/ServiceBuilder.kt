package com.example.roadmaintenance.api


import com.example.roadmaintenance.BuildConfig
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ServiceBuilder {

    private var serverUrl: String? = BuildConfig.SERVER_URL
    private var apiUrl: String? =
        "http://www.mapquestapi.com/directions/v2/"

    private val client = OkHttpClient.Builder().build()

    private val retrofitLightpost = Retrofit.Builder()
        .baseUrl(serverUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    private val retrofitRoadData = Retrofit.Builder()
        .baseUrl(apiUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun <T> buildLightPostService(service: Class<T>): T {
        return retrofitLightpost.create(service)
    }

    fun <T> buildPathPointsService(service: Class<T>): T {
        return retrofitRoadData.create(service)
    }
}