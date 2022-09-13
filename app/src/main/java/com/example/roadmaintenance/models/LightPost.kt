package com.example.roadmaintenance.models

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.roadmaintenance.models.enums.LightPostSides
import com.example.roadmaintenance.models.enums.LightPostStatus
import kotlinx.parcelize.Parcelize

@Parcelize
data class Road(val roadId: Double) : Parcelable

@Parcelize
@Entity(tableName = "light_post_tb")
data class LightPost(
    @PrimaryKey
    val columnId: Long,
    val lightPostId: Double,
    val sides: LightPostSides,
    val height: Double,
    val power: Double,
    val lightProductionType: String,
    val status: LightPostStatus,
    val causeOfFailure: String?,
    val contractingCompany: String,
    val costs: Long,
    val registrationDate: String,
    val registrationTime: String,
    @Embedded
    var registeredRoad: Road
) : Parcelable
