package com.example.roadmaintenance.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roadmaintenance.*
import com.example.roadmaintenance.adapter.LightPostAdapter
import com.example.roadmaintenance.databinding.FragmentLightPostBinding
import com.example.roadmaintenance.models.Pathway
import com.google.android.material.floatingactionbutton.FloatingActionButton


class LightPostFragment : Fragment() {

    var pathway: Pathway? = null
        set(value) {
            field = value
            lightPostAdapter?.setLightPosts(pathway?.lightPosts?.toMutableList())
        }

    private lateinit var showPathOnMap: FloatingActionButton
    private lateinit var binding: FragmentLightPostBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var navController: NavController

    private var lightPostAdapter: LightPostAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentLightPostBinding.inflate(inflater, container, false)

        configPathListRecyclerView()

        (activity as AppCompatActivity).supportActionBar?.hide()

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        navController = view.findNavController()

        setFragmentResultListener(SEND_PATHWAY) { requestKey, bundle ->
            pathway = bundle.getParcelable(SEND_SELECTED_PATHWAY)
            setData()
        }

        binding.toolbar.setupWithNavController(navController)
        showPathOnMap = binding.showPathOnMap
    }

    private fun setData() {
        pathway?.let {
            binding.pathId.text = "#${it.pathId.toInt()}"
            binding.pathWidth.text = "${it.width.toInt()} M"
            binding.distanceBetweenLp.text = "${it.distanceEachLightPost.toInt()} M"
            binding.cable.text = "${it.cablePass}"

            showPathOnMap.setOnClickListener { view ->
                val bundle = Bundle()
                bundle.putParcelable(SEND_SELECTED_PATHWAY, it)
                setFragmentResult(SEND_PATHWAY, bundle)
                navController.navigate(R.id.action_lightPostFragment_to_map_navigation)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as AppCompatActivity).supportActionBar?.show()
    }

}