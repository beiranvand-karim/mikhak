package com.example.roadmaintenance.models;

import android.os.Parcel
import android.os.Parcelable

public class Pathway(
    val columnId: Long,
    val pathId: Double,
    val firstPoint: String,
    val secondPoint: String,
    val width: Double,
    val distanceEachLightPost: Double,
    val cablePass: String,
    val lightPosts: List<LightPost>
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.createTypedArrayList(LightPost)!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(columnId)
        parcel.writeDouble(pathId)
        parcel.writeString(firstPoint)
        parcel.writeString(secondPoint)
        parcel.writeDouble(width)
        parcel.writeDouble(distanceEachLightPost)
        parcel.writeString(cablePass)
        parcel.writeTypedList(lightPosts)
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

