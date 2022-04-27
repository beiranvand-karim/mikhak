package com.example.roadmaintenance.fragments

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.activityViewModels
import com.example.roadmaintenance.R
import com.example.roadmaintenance.map.TypeAndStyles
import com.example.roadmaintenance.models.Pathway
import com.example.roadmaintenance.viewmodels.RequestManager

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*

class MapsFragment : Fragment() {

    private lateinit var googleMap: GoogleMap
    private val typeAndStyles: TypeAndStyles by lazy {
        TypeAndStyles(requireContext())
    }
    private val requestManager: RequestManager by activityViewModels()
    private var pathList: List<Pathway>? = null

    private val mapCallback = OnMapReadyCallback { map ->
        googleMap = map
        typeAndStyles.setTransportationStyle(googleMap)
        val karganeh = LatLng(33.48210320115021, 48.39051692267985)
        val polisRah = LatLng(33.47097058470264, 48.440127059938256)

        map.addMarker(MarkerOptions().position(karganeh).title("کرگانه"))
        map.addMarker(MarkerOptions().position(polisRah).title("پلیس راه"))

        val bounds = LatLngBounds.builder().include(polisRah).include(karganeh).build()
        map.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 0))

        map.uiSettings.apply {
            isZoomControlsEnabled = true
            isZoomGesturesEnabled = true
        }
        map.setPadding(0, 0, 0, 15)

        /// new
        map.isTrafficEnabled = true
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
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment?
        mapFragment?.getMapAsync(mapCallback)

        requestManager.fetchResponse.observe(requireActivity()) {
            pathList = it.body()
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
}