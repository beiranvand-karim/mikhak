package com.example.roadmaintenance.retrofitServices.serviceFactory

import com.example.roadmaintenance.BuildConfig
import com.example.roadmaintenance.auth.BasicAuthInterceptor
import com.example.roadmaintenance.models.User
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

class LoginServiceFactory(private val user: User) : ServiceDetailsFactory() {

    override fun getClient(): OkHttpClient = OkHttpClient.Builder()
        .readTimeout(70, TimeUnit.SECONDS)
        .connectTimeout(70, TimeUnit.SECONDS)
        .addInterceptor(BasicAuthInterceptor(user))
        .build()

    override fun getUrl(): String = BuildConfig.SERVER_URL
}