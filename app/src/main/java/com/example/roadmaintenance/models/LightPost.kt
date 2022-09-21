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
    @PrimaryKey(autoGenerate = true)
    val columnId: Long,
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
    @ColumnInfo(defaultValue = "1")
    var isSyncWithServer: Int = 1
) : Parcelable {
    var registrationDate: String = getDate()
    var registrationTime: String = getTime()

}
