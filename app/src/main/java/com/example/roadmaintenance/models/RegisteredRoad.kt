package com.example.roadmaintenance.models

import android.os.Parcelable
import androidx.room.*
import com.example.roadmaintenance.models.enums.CablePass
import com.example.roadmaintenance.util.getDate
import com.example.roadmaintenance.util.getTime
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "road_tb")
data class RegisteredRoad(
    @PrimaryKey(autoGenerate = true)
    val columnId: Long,
    val roadId: Double,
    val width: Double?,
    val distanceEachLightPost: Double?,
    val cablePass: CablePass?,
    var lightPostCounts: Int?,
    var points: List<CustomPoint>?,
    @ColumnInfo(defaultValue = "1")
    var isSyncWithServer: Int = 1
) : Parcelable {

    @Ignore // room db doesn't support (many to one) relation in this way and we don't need this relation in here.
    var lightPosts: List<LightPost>? = null

    @Embedded
    var roadPath: RoadPath? = null
        set(value) {
            if (value != null)
                field = value
        }

    var registrationDate: String = getDate()
    var registrationTime: String = getTime()

    val latitude_1 get() = points?.first()?.lat
    val longitude_1 get() = points?.first()?.lng
    val latitude_2 get() = points?.last()?.lat
    val longitude_2 get() = points?.last()?.lng
}
