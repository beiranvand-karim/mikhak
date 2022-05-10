package com.example.roadmaintenance.map

import android.graphics.Color
import android.graphics.Paint
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import kotlin.random.Random

class Draw {

    fun drawPathways(map: GoogleMap, latLngList: List<LatLng>) {

        map.addMarker(MarkerOptions().position(latLngList.first()))
        map.addMarker(MarkerOptions().position(latLngList.last()))

        var poly = PolylineOptions()
            .color(Color.rgb(
                Random.nextInt(0, 256),
                Random.nextInt(0, 256),
                Random.nextInt(0, 256)
            ))
            .width(20f)
            .clickable(true)
            .addAll(latLngList)

        map.addPolyline(poly)

        map.setOnPolylineClickListener {
            println(it.id)
        }
    }

}