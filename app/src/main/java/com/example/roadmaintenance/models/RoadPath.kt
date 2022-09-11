package com.example.roadmaintenance.models

import android.os.Parcelable
import androidx.room.Ignore
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class RoadPath(
    val id: Double,
    var region: String?,
) : Parcelable {

    @Ignore
    var segments: List<LatLng>? = emptyList()
}