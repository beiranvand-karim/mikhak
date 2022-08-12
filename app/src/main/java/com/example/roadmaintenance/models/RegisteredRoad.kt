package com.example.roadmaintenance.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
class RegisteredRoad(
    val columnId: Long,
    val roadId: Double,
    val latitude_1: Double,
    val longitude_1: Double,
    val latitude_2: Double,
    val longitude_2: Double,
    val width: Double,
    val distanceEachLightPost: Double,
    val cablePass: String,
    val lightPosts: List<LightPost>,
    var roadData: RoadData? = null
) : Parcelable