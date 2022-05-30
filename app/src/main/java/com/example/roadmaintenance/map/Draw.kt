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

        val randomColor = Color.argb(
            250,
            Random.nextInt(0, 256),
            Random.nextInt(0, 256),
            Random.nextInt(0, 256)
        )

        var poly = createPoly(latLngList, randomColor)

        val firstPoint = createPoint(latlng = latLngList.first(), randomColor)
        val secondPoint = createPoint(latlng = latLngList.last(), randomColor)

        map.addCircle(secondPoint)
        map.addCircle(firstPoint)

        map.setOnPolylineClickListener {
            println(it.id)
        }

        map.addPolyline(poly)

        return poly
    }

    private fun createPoly(latlngs: List<LatLng>, randomColor: Int): PolylineOptions {
        return PolylineOptions().apply {
            color(randomColor)
            width(15f)
            clickable(true)
            addAll(latlngs)
            geodesic(true)
            zIndex(1f)
            jointType(JointType.ROUND)
            startCap(RoundCap())
            endCap(RoundCap())
        }
    }

    private fun createPoint(latlng: LatLng, color: Int): CircleOptions {
        return CircleOptions().apply {
            center(latlng)
            fillColor(Color.MAGENTA)
            radius(100.0)
            strokeColor(color)
            strokeWidth(10f)
            zIndex(2f)
        }
    }
}