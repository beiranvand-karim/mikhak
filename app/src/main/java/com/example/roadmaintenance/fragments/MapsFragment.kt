package com.example.roadmaintenance.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResultListener
import androidx.lifecycle.lifecycleScope
import com.example.roadmaintenance.*
import com.example.roadmaintenance.map.Draw
import com.example.roadmaintenance.map.TypeAndStyles
import com.example.roadmaintenance.models.Pathway
import com.example.roadmaintenance.services.RouteResponseMapper
import com.example.roadmaintenance.viewmodels.PathApi
import com.example.roadmaintenance.viewmodels.RoutingApi
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.*
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MapsFragment : Fragment() {

    private lateinit var googleMap: GoogleMap

    // helper classes
    private val typeAndStyles: TypeAndStyles by lazy {
        TypeAndStyles(requireContext())
    }
    private val draw: Draw by lazy { Draw() }

    private val pathApi: PathApi by activityViewModels()
    private val routing: RoutingApi by activityViewModels()
    private var pathList: List<Pathway>? = null
    private var selectedPath: Pathway? = null

    private val mapCallback = OnMapReadyCallback { map ->

        // default settings
        googleMap = map

        typeAndStyles.setTransportationStyle(googleMap)

        animateCameraToBasePos(googleMap)

        map.uiSettings.apply {
            isZoomControlsEnabled = true
            isZoomGesturesEnabled = true
        }
        map.setPadding(0, 0, 0, 15)
        map.isTrafficEnabled = true

        routing.latLngList.observe(requireActivity()) {
            it?.let {
                draw.drawPathways(googleMap, it)
            }
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

        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment?
        mapFragment?.getMapAsync(mapCallback)

        setFragmentResultListener(SEND_PATHWAY) { requestKey, bundle ->
            selectedPath = bundle.getParcelable(SEND_SELECTED_PATHWAY)
        }

        pathApi.fetchResponse.observe(requireActivity()) { response ->
            response.body()?.let {
                pathList = it
                pathList?.forEach { pathway ->
                    routing.fetchPoints(pathway)
                }
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
                CameraUpdateFactory.newLatLngZoom(LatLng(33.472404, 48.353233), 8f),
                5000,
                object : GoogleMap.CancelableCallback {
                    override fun onCancel() {
                    }

                    override fun onFinish() {
                        selectedPath?.let {
                            map.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        it.latitude_1,
                                        it.longitude_1
                                    ), 12f
                                ), 3000, null
                            )
                        }
                    }

                }
            )
        }
    }
}
