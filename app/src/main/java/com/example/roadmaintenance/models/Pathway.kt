package com.example.roadmaintenance.models;

import android.os.Parcel
import android.os.Parcelable

public class Pathway(
    val columnId: Long,
    val pathId: Double,
    val latitude_1: Double,
    val longitude_1: Double,
    val latitude_2: Double,
    val longitude_2: Double,
    val width: Double,
    val distanceEachLightPost: Double,
    val cablePass: String,
    val lightPosts: List<LightPost>,
    var routeShape : RouteShape? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.createTypedArrayList(LightPost)!!,
        parcel.readParcelable(RouteShape::class.java.classLoader)
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(columnId)
        parcel.writeDouble(pathId)
        parcel.writeDouble(latitude_1)
        parcel.writeDouble(longitude_1)
        parcel.writeDouble(latitude_2)
        parcel.writeDouble(longitude_2)
        parcel.writeDouble(width)
        parcel.writeDouble(distanceEachLightPost)
        parcel.writeString(cablePass)
        parcel.writeTypedList(lightPosts)
        parcel.writeParcelable(routeShape, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Pathway> {
        override fun createFromParcel(parcel: Parcel): Pathway {
            return Pathway(parcel)
        }

        override fun newArray(size: Int): Array<Pathway?> {
            return arrayOfNulls(size)
        }
    }

}
