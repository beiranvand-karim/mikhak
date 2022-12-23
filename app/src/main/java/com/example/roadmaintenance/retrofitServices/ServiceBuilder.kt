package com.example.roadmaintenance.retrofitServices

import com.example.roadmaintenance.retrofitServices.serviceFactory.ServiceDetailsFactory
import retrofit2.Retrofit

class ServiceBuilder(serviceFactory: ServiceDetailsFactory) {

    private val retrofit = Retrofit.Builder()
        .baseUrl(serviceFactory.getUrl())
        .addConverterFactory(serviceFactory.getGsonConverterFactory())
        .client(serviceFactory.getClient())
        .build()

    fun <T> buildService(service: Class<T>): T {
        return retrofit.create(service)
    }
}