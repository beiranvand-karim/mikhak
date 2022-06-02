package com.example.roadmaintenance.map

import android.content.Context
import android.util.Log
import android.view.MenuItem
import com.example.roadmaintenance.R
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.MapStyleOptions
import java.lang.Exception

object TypeAndStyles {

    fun setMapType(context : Context,item: MenuItem, googleMap: GoogleMap) {
        when (item.itemId) {
            R.id.retro_style -> setRetroStyle(context,googleMap)
            R.id.night_style -> setNightStyle(context,googleMap)
            R.id.transportation_style -> setTransportationStyle(context,googleMap)
            R.id.hybrid_map -> googleMap.mapType = GoogleMap.MAP_TYPE_HYBRID
            R.id.satellite_map -> googleMap.mapType = GoogleMap.MAP_TYPE_SATELLITE
        }
    }

    fun setTransportationStyle(context : Context,googleMap: GoogleMap) {
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        try {
            val isSuccess = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    context,
                    R.raw.map_transportation_style
                )
            )
            if (!isSuccess) {
                Log.e("Style", "Can not load retro style")
            }
        } catch (e: Exception) {
            Log.e("Load Style", "style loading error")
            e.printStackTrace().toString()
        }
    }

    private fun setRetroStyle(context : Context,googleMap: GoogleMap) {
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        try {
            val isSuccess = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(context, R.raw.map_retro_style)
            )
            if (!isSuccess) {
                Log.e("Style", "Can not load retro style")
            }
        } catch (e: Exception) {
            Log.e("Load Style", "style loading error")
            e.printStackTrace().toString()
        }
    }

    private fun setNightStyle(context : Context,googleMap: GoogleMap) {
        googleMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        try {
            val isSuccess = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(context, R.raw.map_night_style)
            )
            if (!isSuccess) {
                Log.e("Style", "Can not load retro style")
            }
        } catch (e: Exception) {
            Log.e("Load Style", "style loading error")
            e.printStackTrace().toString()
        }
    }

}