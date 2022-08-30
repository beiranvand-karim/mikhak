package com.example.roadmaintenance.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roadmaintenance.R
import com.example.roadmaintenance.adapter.BottomSheetListAdapter
import com.example.roadmaintenance.databinding.FragmentMapsLayoutBinding
import com.example.roadmaintenance.models.RegisteredRoad
import com.example.roadmaintenance.models.RoadPath
import com.example.roadmaintenance.network.NetworkConnection
import com.example.roadmaintenance.viewmodels.RoadViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MapsLayout : Fragment() {

    private val args by navArgs<MapsLayoutArgs>()
    private val mapsFragment: MapsFragment by lazy {
        MapsFragment()
    }
    var selectedRoad: RegisteredRoad? = null
    set(value) {
        value?.let {
            field = value
            mapsFragment.selectedRoad = field
        }
    }
    var registeredRoads: Array<RegisteredRoad> = emptyArray()
    private var _binding: FragmentMapsLayoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private val bottomSheetListAdapter: BottomSheetListAdapter by lazy { BottomSheetListAdapter() }
    private lateinit var bottomSheet: LinearLayout
    private val roadViewModel: RoadViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapsLayoutBinding.inflate(inflater, container, false)

        if (!NetworkConnection.IsInternetAvailable) {
            Toast.makeText(
                requireContext().applicationContext,
                "You are in offline mode",
                Toast.LENGTH_LONG
            ).show()
        }

        configBottomSheet()
        configRecyclerList()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        registeredRoads = args.registeredRoads
        selectedRoad = args.selectedRoad

        registeredRoads.apply {
            if (this.isNullOrEmpty()) {
                roadViewModel.roads.observe(viewLifecycleOwner) {
                    registeredRoads = it.toTypedArray()
                }
            }
            loadRoads()
        }

        childFragmentManager
            .beginTransaction()
            .replace(R.id.map_placeholder, mapsFragment)
            .commit()
    }

    private fun loadRoads() {
        showRecyclerView()
        bottomSheetListAdapter.registeredRoads = registeredRoads.toList()
        mapsFragment.registeredRoads = registeredRoads
        mapsFragment.selectedRoad = selectedRoad
    }

    private fun configBottomSheet() {
        bottomSheet = binding.mapBottomSheet.root
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isFitToContents = true
    }

    private fun showRecyclerView() {
        binding.mapBottomSheet.emptyListWarning.visibility = View.GONE
        binding.mapBottomSheet.bottomSheetRecyclerview.visibility = View.VISIBLE
    }

    private fun configRecyclerList() {
        recyclerView = binding.mapBottomSheet.bottomSheetRecyclerview
        recyclerView.layoutManager = LinearLayoutManager(
            requireContext().applicationContext,
            LinearLayoutManager.VERTICAL,
            false
        )
        recyclerView.adapter = bottomSheetListAdapter
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}