package com.example.roadmaintenance.auth

import com.example.roadmaintenance.models.User
import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Response

class BasicAuthInterceptor(user: User) : Interceptor {
    private val credentials = Credentials.basic(
        user.id,
        user.password!!
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        request = request.newBuilder().header("Authorization", credentials).build()
        return chain.proceed(request)
    }
}