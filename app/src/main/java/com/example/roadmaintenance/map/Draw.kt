package com.example.roadmaintenance.map

import android.graphics.Color
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import kotlin.random.Random

object DrawHelper {

    fun drawPathways(
        map: GoogleMap,
        latLngList: List<LatLng>
    ): PolylineOptions {

        var poly = PolylineOptions().apply {
            color(
                Color.argb(
                    250,
                    Random.nextInt(0, 256),
                    Random.nextInt(0, 256),
                    Random.nextInt(0, 256)
                )
            )
            width(35f)
            clickable(true)
            addAll(latLngList)
            geodesic(true)
            zIndex(1f)
            jointType(JointType.ROUND)
            startCap(RoundCap())
            endCap(RoundCap())
        }

        map.setOnPolylineClickListener {
            println(it.id)
        }

        map.addPolyline(poly)

        return poly
    }

}