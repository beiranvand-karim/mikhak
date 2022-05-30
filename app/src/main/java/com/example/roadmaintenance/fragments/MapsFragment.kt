package com.example.roadmaintenance.fragments

import android.content.res.TypedArray
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import com.example.roadmaintenance.*
import com.example.roadmaintenance.R
import com.example.roadmaintenance.map.DrawHelper
import com.example.roadmaintenance.map.TypeAndStyles
import com.example.roadmaintenance.models.Pathway
import com.example.roadmaintenance.models.RouteShape
import com.example.roadmaintenance.viewmodels.MapViewModel
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch


class MapsFragment : Fragment(), OnMapReadyCallback {

    private val asiaPos = LatLng(33.46253129247596, 48.3542241356822)
    private val lorestanPos = LatLng(33.46253129247596, 48.3542241356822)
    private val lorestanCameraPos = CameraPosition(lorestanPos, 8f, 0f, 0f)

    private lateinit var googleMap: GoogleMap
    private val mapViewModel: MapViewModel by activityViewModels()

    // helper classes
    private val typeAndStyles: TypeAndStyles by lazy {
        TypeAndStyles(requireContext())
    }
    private var routesShapes: MutableList<RouteShape> = arrayListOf()
    private var pathList: List<Pathway>? = null
    private var selectedPath: Pathway? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_maps, container, false)
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
            pathArray.let {
                pathList = it.toMutableList()
                mapViewModel.getRoutesData(pathList!!)
            }
        }

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

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
                    ), 12f
                ),
                2000,
                null
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
            mapViewModel.pathCoordinates.collect { routesShapeList ->
                routesShapeList?.forEach {
                    routesShapes.add(it)
                    DrawHelper.drawPathways(googleMap, it.segments)
                }
            }
        }
    }
}
