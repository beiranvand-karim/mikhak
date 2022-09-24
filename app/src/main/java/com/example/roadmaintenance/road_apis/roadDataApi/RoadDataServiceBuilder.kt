package com.example.roadmaintenance.road_apis.roadDataApi


import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val client: OkHttpClient = OkHttpClient.Builder().build()

object RoadDataServiceBuilder {

    private var apiUrl: String? =
        "http://www.mapquestapi.com/directions/v2/"

    private val retrofitRoadData = Retrofit.Builder()
        .baseUrl(apiUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun <T> buildRoadsDataService(service: Class<T>): T {
        return retrofitRoadData.create(service)
    }
}