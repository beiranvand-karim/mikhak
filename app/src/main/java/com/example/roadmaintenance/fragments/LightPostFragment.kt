package com.example.roadmaintenance.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.setFragmentResultListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roadmaintenance.R
import com.example.roadmaintenance.REQUEST_KEY_PASS_DATA_TO_LIGHT_POST
import com.example.roadmaintenance.REQUEST_KEY_PASS_PATHWAY
import com.example.roadmaintenance.adapter.LightPostAdapter
import com.example.roadmaintenance.adapter.PathListAdapter
import com.example.roadmaintenance.databinding.FragmentLightPostBinding
import com.example.roadmaintenance.databinding.LightpostCardViewBinding
import com.example.roadmaintenance.models.Pathway

class LightPostFragment : Fragment() {

    var pathway: Pathway? = null
        set(value) {
            field = value
            lightPostAdapter?.setLightPosts(pathway?.lightPosts?.toMutableList())
        }

    private lateinit var binding: FragmentLightPostBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private var lightPostAdapter: LightPostAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLightPostBinding.inflate(inflater, container, false)
        configPathListRecyclerView()
        setFragmentResultListener(REQUEST_KEY_PASS_DATA_TO_LIGHT_POST) { requestKey, bundle ->
            pathway = bundle.getParcelable(REQUEST_KEY_PASS_PATHWAY)
        }
        return binding.root
    }

    private fun configPathListRecyclerView() {
        recyclerView = binding.lightPostRecyclerView

        linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        lightPostAdapter = LightPostAdapter(pathway?.lightPosts?.toMutableList())

        recyclerView.apply {
            layoutManager = linearLayoutManager
            adapter = lightPostAdapter
        }
    }

}