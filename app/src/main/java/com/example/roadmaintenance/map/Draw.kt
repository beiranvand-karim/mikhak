package com.example.roadmaintenance.map

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import android.location.Geocoder
import android.os.Build
import android.provider.CalendarContract
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.core.graphics.red
import com.example.roadmaintenance.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.*
import com.google.maps.android.heatmaps.Gradient
import com.google.maps.android.heatmaps.HeatmapTileProvider
import kotlin.math.abs
import kotlin.random.Random

object DrawHelper {

    fun drawPathways(
        context: Context,
        map: GoogleMap,
        latLngList: List<LatLng>
    ) {

        addHeatMap(context, map, latLngList)
        var poly = PolylineOptions().apply {
            color(
                Color.argb(
                    100,
                    Random.nextInt(0, 256),
                    Random.nextInt(0, 256),
                    Random.nextInt(0, 256)
                )
            )
            width(40f)
            clickable(true)
            addAll(latLngList)
            geodesic(true)
            zIndex(1f)
            jointType(JointType.ROUND)
            startCap(RoundCap())
            endCap(RoundCap())
        }



        map.addPolyline(poly)

        map.setOnPolylineClickListener {
            println(it.id)
        }
    }

    private fun addHeatMap(
        context: Context,
        map: GoogleMap,
        latLngList: List<LatLng>
    ) {

        val colors = intArrayOf(
            ContextCompat.getColor(context, R.color.pathLight),
            ContextCompat.getColor(context, R.color.accent)
        )
        val startPoints = floatArrayOf(.2f, .5f)

        val gradient = Gradient(colors, startPoints)

        val provider = HeatmapTileProvider
            .Builder()
            .gradient(gradient)
            .data(latLngList)
            .radius(30)
            .build()

        map.addTileOverlay(
            TileOverlayOptions()
                .tileProvider(provider)
                .fadeIn(true)
                .transparency(.20f)
                .zIndex(.1f)
        )

    }

}