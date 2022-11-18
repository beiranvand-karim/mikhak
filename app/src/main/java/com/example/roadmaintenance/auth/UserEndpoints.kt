package com.example.roadmaintenance.auth

import okhttp3.ResponseBody
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface UserEndpoints {

    @POST("check/{id}")
    suspend fun checkUser(@Path(value = "id") id: String): String

    @POST("check/")
    suspend fun verifyUser(
        @Query(value = "verifyCode") verifyCode: Int,
        @Query("password") password: String
    ): String

    @POST("logIn")
    suspend fun login(): ResponseBody

}