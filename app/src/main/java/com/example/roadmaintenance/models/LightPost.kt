package com.example.roadmaintenance.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class LightPost(
    val columnId: Long,
    val lightPostId: Double,
    val sides: String,
    val height: Double,
    val power: Double,
    val lightProductionType: String,
    val road: RegisteredRoad
) : Parcelable

