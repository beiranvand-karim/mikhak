package com.example.roadmaintenance.models

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class RoadPath(
    val id: Double,
    var region: String?,
    @Ignore
    var segments: List<LatLng>?
) :
    Parcelable