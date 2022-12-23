package com.example.roadmaintenance.retrofitServices.serviceFactory

import com.example.roadmaintenance.BuildConfig
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import retrofit2.converter.gson.GsonConverterFactory

class BackendServiceFactory : ServiceDetailsFactory() {

    override fun getUrl(): String = BuildConfig.SERVER_URL

    override fun getGsonConverterFactory(): GsonConverterFactory {
        val gson: Gson = GsonBuilder()
            .setDateFormat("HH:mm:ss")
            .setDateFormat("yyyy-mm-dd")
            .create()
        return GsonConverterFactory.create(gson)
    }

}