package com.example.roadmaintenance.models

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
data class Path(val pathId: Double) : Parcelable

@Parcelize
@Entity(tableName = "light_post_tb")
data class LightPost(
    @PrimaryKey(autoGenerate = true)
    val lpColumnId: Long,
    val lightPostId: Double,
    val sides: String,
    val height: Double,
    val power: Double,
    val lightProductionType: String,
    @Embedded
    var path: Path
) : Parcelable {

}
