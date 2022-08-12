package com.example.roadmaintenance.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roadmaintenance.R
import com.example.roadmaintenance.RESTORE_PATHWAYS
import com.example.roadmaintenance.adapter.BottomSheetListAdapter
import com.example.roadmaintenance.databinding.FragmentMapsLayoutBinding
import com.example.roadmaintenance.models.Pathway
import com.google.android.material.bottomsheet.BottomSheetBehavior

class MapsLayout : Fragment() {

    private val args by navArgs<MapsLayoutArgs>()

    private val mapsFragment: MapsFragment by lazy {
        MapsFragment()
    }

    lateinit var selectedPath: Pathway
    lateinit var pathArray: Array<Pathway>
    private var _binding: FragmentMapsLayoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private val bottomSheetListAdapter: BottomSheetListAdapter by lazy { BottomSheetListAdapter() }
    private lateinit var bottomSheet: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMapsLayoutBinding.inflate(inflater, container, false)

        pathArray = args.pathways
        selectedPath = args.selectedPath

        childFragmentManager
            .beginTransaction()
            .replace(R.id.map_placeholder, mapsFragment)
            .commit()

        configBottomSheet()

        configRecyclerList()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapsFragment.pathArray = pathArray
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

        pathArray
            .takeUnless { it.isNullOrEmpty() }
            ?.apply {
                showRecyclerView()
                bottomSheetListAdapter.pathList = pathArray.toList()
            }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArray(RESTORE_PATHWAYS, pathArray)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val nullablePathways =
            savedInstanceState?.getParcelableArray(RESTORE_PATHWAYS) as Array<Pathway>?
        nullablePathways?.let {
            showRecyclerView()
            pathArray = it
            bottomSheetListAdapter.pathList = it.toList()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}