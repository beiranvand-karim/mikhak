package com.example.roadmaintenance.retrofitServices.serviceFactory

import com.example.roadmaintenance.BuildConfig
import com.example.roadmaintenance.auth.BasicAuthInterceptor
import com.example.roadmaintenance.models.User
import okhttp3.OkHttpClient

class LoginServiceFactory(private val user: User) : ServiceDetailsFactory() {

    override fun getClient(): OkHttpClient = OkHttpClient
        .Builder()
        .addInterceptor(BasicAuthInterceptor(user))
        .build()

    override fun getUrl(): String = BuildConfig.SERVER_URL
}