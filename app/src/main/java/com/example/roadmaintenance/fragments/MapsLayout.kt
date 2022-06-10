package com.example.roadmaintenance.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roadmaintenance.*
import com.example.roadmaintenance.adapter.BottomSheetListAdapter
import com.example.roadmaintenance.adapter.LightPostAdapter
import com.example.roadmaintenance.databinding.FragmentMapsLayoutBinding
import com.example.roadmaintenance.models.Pathway
import com.google.android.material.bottomsheet.BottomSheetBehavior

class MapsLayout : Fragment() {

    private val mapsFragment: MapsFragment by lazy {
        MapsFragment()
    }
    var selectedPath: Pathway? = null
    set(value) {
        if (value != null){
            field = value
            mapsFragment.selectedPath = value
        }
    }
    var pathArray: Array<Pathway>? = null
    set(value) {
        if (value != null){
            field = value
            mapsFragment.pathArray = value
        }
    }
    private var _binding: FragmentMapsLayoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private val bottomSheetListAdapter: BottomSheetListAdapter by lazy { BottomSheetListAdapter() }
    private lateinit var bottomSheet: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMapsLayoutBinding.inflate(inflater, container, false)

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

        setFragmentResultListener(SEND_SELECTED_PATHWAY) { requestKey, bundle ->
            selectedPath = bundle.getParcelable<Pathway>(SEND_SELECTED_PATHWAY)
            selectedPath?.let {
                mapsFragment.selectedPath = it
            }

            bottomSheet = binding.mapBottomSheet.root
            val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
            bottomSheetBehavior.isFitToContents = true

        }
        setFragmentResultListener(SEND_PATHWAY_LIST) { requestKey, bundle ->
            bundle.getParcelableArray(SEND_PATHWAY_LIST)?.also {
                pathArray = it as Array<Pathway>
                mapsFragment.pathArray = it
                bottomSheetListAdapter.pathList = it.toMutableList()
            }
        }
    }

    private fun configBottomSheet(){
        bottomSheet = binding.mapBottomSheet.root
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.isFitToContents = true
    }
    private fun configRecyclerList() {
        binding.mapBottomSheet.emptyListWarning.visibility = View.GONE
        binding.mapBottomSheet.bottomSheetRecyclerview.visibility = View.VISIBLE

        recyclerView = binding.mapBottomSheet.bottomSheetRecyclerview
        recyclerView.layoutManager = LinearLayoutManager(
            requireContext().applicationContext,
            LinearLayoutManager.VERTICAL,
            false
        )

        recyclerView.adapter = bottomSheetListAdapter
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelableArray(RESTORE_PATHWAY_LIST, pathArray)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        val nullablePathList =
            savedInstanceState?.getParcelableArray(RESTORE_PATHWAY_LIST) as Array<Pathway>?
        nullablePathList?.let {
            pathArray = it
            bottomSheetListAdapter.pathList = it.toMutableList()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}