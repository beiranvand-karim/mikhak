package com.example.roadmaintenance.retrofitServices.serviceFactory

import okhttp3.OkHttpClient
import retrofit2.converter.gson.GsonConverterFactory

abstract class ServiceDetailsFactory {

    open fun getClient(): OkHttpClient = OkHttpClient.Builder().build()
    abstract fun getUrl(): String
    open fun getGsonConverterFactory(): GsonConverterFactory = GsonConverterFactory.create()

}