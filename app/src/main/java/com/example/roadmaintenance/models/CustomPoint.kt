package com.example.roadmaintenance.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class CustomPoint(
    var lat: Double,
    var lng: Double
) : Parcelable {

    constructor(str: String) : this(0.0, 0.0) {
        val points = str.split(",")
        lat = points.first().toDouble()
        lng = points.last().toDouble()
    }

    override fun toString(): String = "$lat,$lng"
}
