package com.example.roadmaintenance.models

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.roadmaintenance.models.enums.CablePass
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "road_tb")
data class RegisteredRoad(
    @PrimaryKey
    val columnId: Long,
    val roadId: Double,
    val width: Double,
    val distanceEachLightPost: Double,
    val cablePass: CablePass,
    // Date types are in string because they are final and read only
    var registrationDate: String,
    var registrationTime: String,
    var lightPostCounts: Int,
    var points: List<CustomPoint>
) : Parcelable {

    @Ignore // room db doesn't support many to one in this way and we don't need this relation in here.
    var lightPosts: List<LightPost>? = null

    @Embedded
    var roadPath: RoadPath? = null
        set(value) {
            if (value != null)
                field = value
        }

    val latitude_1
        get() = points.first().lat
    val longitude_1 get() = points.first().lng
    val latitude_2 get() = points.last().lat
    val longitude_2 get() = points.last().lng
}
