package com.example.roadmaintenance.map

import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*

class Draw {

    fun drawPathways(map: GoogleMap, latLngList: List<LatLng>) {
        map.addMarker(MarkerOptions().position(latLngList.first()))
        map.addMarker(MarkerOptions().position(latLngList.last()))
    }

}