package com.example.roadmaintenance.models;

import android.os.Parcel
import android.os.Parcelable

data class LightPost(
    val columnId: Long,
    val lightPostId: Double,
    val sides: String,
    val height: Double,
    val power: Double,
    val lightProductionType: String,
    val path: Pathway
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readString()!!,
        parcel.readParcelable(Pathway::class.java.classLoader)!!
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(columnId)
        parcel.writeDouble(lightPostId)
        parcel.writeString(sides)
        parcel.writeDouble(height)
        parcel.writeDouble(power)
        parcel.writeString(lightProductionType)
        parcel.writeParcelable(path, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LightPost> {
        override fun createFromParcel(parcel: Parcel): LightPost {
            return LightPost(parcel)
        }

        override fun newArray(size: Int): Array<LightPost?> {
            return arrayOfNulls(size)
        }
    }

}
