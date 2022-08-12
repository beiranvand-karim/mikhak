package com.example.roadmaintenance.models

import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
data class RoadData(
    val id: Double,
    val region: ArrayList<String>,
    val segments: List<LatLng>
) :
    Parcelable