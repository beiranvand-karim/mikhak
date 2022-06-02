package com.example.roadmaintenance.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.findNavController
import com.example.roadmaintenance.*
import com.example.roadmaintenance.models.Pathway

class MapsLayout : Fragment() {

    private val mapsFragment: MapsFragment by lazy {
        MapsFragment()
    }
    private var selectedPath: Pathway? = null
    private var pathArray: Array<Pathway>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_maps_layout, container, false)
        childFragmentManager
            .beginTransaction()
            .replace(R.id.map_placeholder, mapsFragment)
            .commit()

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setFragmentResultListener(SEND_SELECTED_PATHWAY) { requestKey, bundle ->
            selectedPath = bundle.getParcelable<Pathway>(SEND_SELECTED_PATHWAY)
            selectedPath?.let {
                mapsFragment.selectedPath = it
            }
        }
        setFragmentResultListener(SEND_PATHWAY_LIST) { requestKey, bundle ->
            bundle.getParcelableArray(SEND_PATHWAY_LIST)?.also {
                pathArray = it as Array<Pathway>
                mapsFragment.pathArray = it
            }
        }

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
        }
    }
}