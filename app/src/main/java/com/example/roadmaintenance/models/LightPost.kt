package com.example.roadmaintenance.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.roadmaintenance.models.enums.LightPostSides
import com.example.roadmaintenance.models.enums.LightPostStatus
import com.example.roadmaintenance.util.getDate
import com.example.roadmaintenance.util.getTime
import kotlinx.parcelize.Parcelize

@Parcelize
data class Road(val roadId: Double) : Parcelable

@Parcelize
@Entity(tableName = "light_post_tb")
data class LightPost(
    @PrimaryKey
    val lightPostId: Double,
    val sides: LightPostSides?,
    val height: Double?,
    val power: Double?,
    val lightProductionType: String?,
    val status: LightPostStatus?,
    val causeOfFailure: String?,
    val contractingCompany: String?,
    val costs: Long?,
    @Embedded
    var registeredRoad: Road,
    @ColumnInfo(defaultValue = "true")
    var isSyncWithServer: String = "true"
) : Parcelable {

    var registrationDate: String = getDate()
    var registrationTime: String = getTime()

}
