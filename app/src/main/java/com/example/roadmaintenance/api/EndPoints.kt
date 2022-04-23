package com.example.roadmaintenance.api

import com.example.roadmaintenance.models.Path
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*
import java.io.File

interface EndPoints {

    @GET("paths")
    fun getUsers(): Call<List<Path>>

    @Multipart
    @POST("upload_file")
    fun uploadFile(@Part part: MultipartBody.Part): Call<ResponseBody>

}