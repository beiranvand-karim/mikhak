package com.example.roadmaintenance.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import com.example.roadmaintenance.*
import com.example.roadmaintenance.map.DrawHelper
import com.example.roadmaintenance.map.TypeAndStyles
import com.example.roadmaintenance.models.Pathway
import com.example.roadmaintenance.viewmodels.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class MapsFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private val mapViewModel: MapViewModel by activityViewModels()

    // helper classes
    private val typeAndStyles: TypeAndStyles by lazy {
        TypeAndStyles(requireContext())
    }
    private var polyList: ArrayList<PolylineOptions> = arrayListOf()
    private var pathList: List<Pathway>? = null
    private var selectedPath: Pathway? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment?

        mapFragment?.getMapAsync(this)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        (activity as AppCompatActivity).supportActionBar?.show()


        setFragmentResultListener(SEND_PATHWAY) { requestKey, bundle ->
            selectedPath = bundle.getParcelable(SEND_SELECTED_PATHWAY)
        }

        setFragmentResultListener(SEND_PATHWAY_LIST) { requestKey, bundle ->
            val pathArray = bundle.getParcelableArray(SEND_PATHWAY_LIST) as Array<Pathway>
            pathArray?.let {
                pathList = it.toMutableList()
                mapViewModel.getRoutesData(pathList!!)
            }
        }

    }


    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.map_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        typeAndStyles.setMapType(item, googleMap)
        return super.onOptionsItemSelected(item)
    }

    private fun animateCameraToBasePos(map: GoogleMap) {
        lifecycleScope.launch {
            delay(2000)
            map.animateCamera(
                CameraUpdateFactory.newLatLng(
                    LatLng(
                        33.46253129247596,
                        48.3542241356822
                    )
                ),
                2000,
                object : GoogleMap.CancelableCallback {
                    override fun onCancel() {
                    }

                    override fun onFinish() {
                        map.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    33.46253129247596,
                                    48.3542241356822
                                ), 8f
                            ), 3000, object : GoogleMap.CancelableCallback {
                                override fun onFinish() {
                                    selectedPath?.let {
                                        map.animateCamera(
                                            CameraUpdateFactory.newLatLngZoom(
                                                LatLng(
                                                    it.latitude_1,
                                                    it.longitude_1
                                                ), 12f
                                            ), 1000, null
                                        )
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


    override fun onMapReady(map: GoogleMap) {
        // default settings
        googleMap = map

        typeAndStyles.setTransportationStyle(googleMap)

        animateCameraToBasePos(googleMap)

        map.uiSettings.apply {
            isZoomControlsEnabled = true
            isZoomGesturesEnabled = true
        }
        map.setPadding(0, 0, 0, 15)

        viewLifecycleOwner.lifecycleScope.launch {
            mapViewModel.pathCoordinates.collectLatest {
                polyList.add(DrawHelper.drawPathways(googleMap, it.segments))
            }
        }

        polyList?.forEach {
            map.addPolyline(it)
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        if (!polyList.isNullOrEmpty()) {
            outState.putParcelableArrayList(RESTORE_POLYLINE_LIST, polyList)
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let { bundle ->
            val nullablePolyList =
                bundle.getParcelableArrayList<PolylineOptions>(RESTORE_POLYLINE_LIST)
            nullablePolyList?.let {
                polyList = it
            }
        }
    }
}
