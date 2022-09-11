package com.example.roadmaintenance.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.roadmaintenance.R
import com.example.roadmaintenance.map.DrawHelper
import com.example.roadmaintenance.map.TypeAndStyles
import com.example.roadmaintenance.models.RegisteredRoad
import com.example.roadmaintenance.viewmodels.RoadViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MapsFragment : Fragment(), OnMapReadyCallback {

    private val asiaPos = LatLng(33.46253129247596, 48.3542241356822)
    private val lorestanPos = LatLng(33.46253129247596, 48.3542241356822)
    private val lorestanCameraPos = CameraPosition(lorestanPos, 8f, 0f, 0f)
    private val CAMERA_POSITION = "CAMERA_POSITION"
    private var mapFragment: SupportMapFragment? = null
    private lateinit var roadViewModel: RoadViewModel
    var registeredRoads: Array<RegisteredRoad> = emptyArray()
    private lateinit var googleMap: GoogleMap
    var selectedRoad: RegisteredRoad? = null
        set(value) {
            if (value != null) {
                field = value
                println(value)
                if (::googleMap.isInitialized)
                    animateCameraToSelectedRoad(googleMap, field!!)
            }
        }

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
            roadViewModel = ViewModelProvider(this)[RoadViewModel::class.java]
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
                    override fun onCancel() {}
                    override fun onFinish() {
                        map.animateCamera(
                            CameraUpdateFactory.newCameraPosition(
                                lorestanCameraPos
                            ), 3000, object : GoogleMap.CancelableCallback {
                                override fun onFinish() {
                                    selectedRoad?.let {
                                        animateCameraToSelectedRoad(map, it)
                                    }
                                }

                                override fun onCancel() {}
                            }
                        )

                    }

                }
            )
        }
    }

    private fun animateCameraToSelectedRoad(map: GoogleMap, road: RegisteredRoad) {
        lifecycleScope.launch {
            delay(1500)
            map.animateCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(
                        road.latitude_1,
                        road.longitude_1
                    ), 14f
                ),
                2000,
                null
            )
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        roadViewModel.getRoadPathSegment(registeredRoads.toList())
        lifecycleScope.launchWhenCreated {
            roadViewModel.roadPathFlow.observe(viewLifecycleOwner) { roadPath ->
                DrawHelper.drawRoadSegmentsOnMap(map, roadPath.segments!!)
            }
        }
        context?.let {
            TypeAndStyles.setTransportationStyle(it.applicationContext, googleMap)
        }

        if (savedCameraPosition != null)
            googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(savedCameraPosition!!))
        else {
            animateCameraToBasePos(googleMap)
            savedCameraPosition = googleMap.cameraPosition
        }

        map.apply {
            uiSettings.apply {
                isZoomControlsEnabled = true
                isZoomGesturesEnabled = true
            }
            setPadding(0, 0, 0, 350)
        }
        selectedRoad?.let {
            animateCameraToSelectedRoad(map, it)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(CAMERA_POSITION, googleMap.cameraPosition)
        savedCameraPosition = googleMap.cameraPosition
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            savedCameraPosition = it.getParcelable(CAMERA_POSITION) as CameraPosition?
        }
    }

    companion object {
        private var savedCameraPosition: CameraPosition? = null
    }
}
