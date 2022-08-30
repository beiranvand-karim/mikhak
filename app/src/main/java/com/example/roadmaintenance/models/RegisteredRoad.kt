package com.example.roadmaintenance.models

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "road_tb")
data class RegisteredRoad(
    @PrimaryKey
    val columnId: Long,
    val pathId: Double,
    val latitude_1: Double,
    val longitude_1: Double,
    val latitude_2: Double,
    val longitude_2: Double,
    val width: Double,
    val distanceEachLightPost: Double,
    val cablePass: String,
    var lightPostCounts: Int,
) : Parcelable {

    @Ignore
    var lightPosts: List<LightPost>? = null

    @Embedded
    var roadPath: RoadPath? = null
        set(value) {
            if (value != null)
                field = value
        }
}
