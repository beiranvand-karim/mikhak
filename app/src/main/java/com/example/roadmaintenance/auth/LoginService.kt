package com.example.roadmaintenance.auth

import com.example.roadmaintenance.BuildConfig
import com.example.roadmaintenance.models.User
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object LoginService {

    private fun clientBuilder(user: User) =
        OkHttpClient.Builder()
            .addInterceptor(BasicAuthInterceptor(user))
            .build()

    private var serverUrl: String? = BuildConfig.SERVER_URL

    private fun retrofitCreator(user: User) = Retrofit.Builder()
        .baseUrl(serverUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .client(clientBuilder(user))
        .build()

    fun <T> buildRegisteredRoadsService(service: Class<T>, user: User): T {
        return retrofitCreator(user).create(service)
    }
}