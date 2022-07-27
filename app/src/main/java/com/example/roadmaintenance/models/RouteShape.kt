package com.example.roadmaintenance.models

import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng

class RouteShape(val id: Double, val region: ArrayList<String>, val segments: List<LatLng>) :
    Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readDouble(),
        parcel.createStringArrayList()!!,
        parcel.createTypedArrayList(LatLng.CREATOR)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(id)
        parcel.writeStringList(region)
        parcel.writeTypedList(segments)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<RouteShape> {
        override fun createFromParcel(parcel: Parcel): RouteShape {
            return RouteShape(parcel)
        }

        override fun newArray(size: Int): Array<RouteShape?> {
            return arrayOfNulls(size)
        }
    }
}