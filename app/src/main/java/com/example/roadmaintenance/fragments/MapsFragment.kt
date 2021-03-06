package com.example.roadmaintenance.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.roadmaintenance.R
import com.example.roadmaintenance.map.DrawHelper
import com.example.roadmaintenance.map.TypeAndStyles
import com.example.roadmaintenance.models.Pathway
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MapsFragment : Fragment(), OnMapReadyCallback {

    private val asiaPos = LatLng(33.46253129247596, 48.3542241356822)
    private val lorestanPos = LatLng(33.46253129247596, 48.3542241356822)
    private val lorestanCameraPos = CameraPosition(lorestanPos, 8f, 0f, 0f)
    private var mapFragment: SupportMapFragment? = null
    private lateinit var googleMap: GoogleMap

    var selectedPath: Pathway? = null
        set(value) {
            if (value != null) {
                field = value
                if (::googleMap.isInitialized)
                    animateCameraToSelectedPath(googleMap, field!!)
            }
        }
    var pathArray: Array<Pathway>? = null


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        (activity as AppCompatActivity).supportActionBar?.show()

        if (mapFragment == null) {
            mapFragment =
                childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment?
            mapFragment?.getMapAsync(this)
        }

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        TypeAndStyles.setMapType(requireContext().applicationContext, item, googleMap)
        return super.onOptionsItemSelected(item)
    }

    private fun animateCameraToBasePos(map: GoogleMap) {
        lifecycleScope.launch {
            delay(2000)
            map.animateCamera(
                CameraUpdateFactory.newLatLng(
                    asiaPos
                ),
                2000,
                object : GoogleMap.CancelableCallback {
                    override fun onCancel() {
                    }

                    override fun onFinish() {
                        map.animateCamera(
                            CameraUpdateFactory.newCameraPosition(
                                lorestanCameraPos
                            ), 3000, object : GoogleMap.CancelableCallback {
                                override fun onFinish() {
                                    selectedPath?.let {
                                        animateCameraToSelectedPath(map, it)
                                    }
                                }

                                override fun onCancel() {
                                }
                            }
                        )

                    }

                }
            )
        }
    }

    private fun animateCameraToSelectedPath(map: GoogleMap, path: Pathway) {
        lifecycleScope.launch {
            delay(2000)
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        path.latitude_1,
                        path.longitude_1
                    ), 14f
                ),
                2000,
                null
            )
        }
    }

    override fun onMapReady(map: GoogleMap) {
        // default settings
        googleMap = map

        context?.let {
            TypeAndStyles.setTransportationStyle(it.applicationContext, googleMap)
        }

        animateCameraToBasePos(googleMap)

        map.uiSettings.apply {
            isZoomControlsEnabled = true
            isZoomGesturesEnabled = true
        }
        map.setPadding(0, 0, 0, 350)

        pathArray?.forEach {
            it.routeShape?.let { routeShape ->
                DrawHelper.drawPathways(googleMap, routeShape.segments)
            }
        }
        selectedPath?.let {
            animateCameraToSelectedPath(map, it)
        }
    }

}
