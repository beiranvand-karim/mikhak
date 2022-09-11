package com.example.roadmaintenance.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roadmaintenance.adapter.LightPostAdapter
import com.example.roadmaintenance.databinding.FragmentLightPostBinding
import com.example.roadmaintenance.models.RegisteredRoad
import com.example.roadmaintenance.viewmodels.RoadViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton


class LightPostFragment : Fragment() {

    private val args by navArgs<LightPostFragmentArgs>()

    private lateinit var registeredRoad: RegisteredRoad

    private lateinit var showRoadOnMap: FloatingActionButton
    private var _binding: FragmentLightPostBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var navController: NavController

    private val lightPostAdapter: LightPostAdapter by lazy { LightPostAdapter() }
    private val roadViewModel: RoadViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLightPostBinding.inflate(inflater, container, false)
        configRoadListRecyclerView()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configHeader(view)

        registeredRoad = args.selectedRoad
        registeredRoad.apply {
            if (this.lightPosts.isNullOrEmpty()) {
                roadViewModel
                    .getLightPostsByRoadId(this.pathId)
                    .observe(viewLifecycleOwner) {
                        this.lightPosts = it
                        loadLightPosts()
                    }
            } else
                loadLightPosts()
        }
    }

    private fun configHeader(view: View) {
        navController = view.findNavController()
        binding.toolbar.setupWithNavController(navController)
        showRoadOnMap = binding.showRoadOnMap
    }

    private fun loadLightPosts() {
        lightPostAdapter.lightPostList = registeredRoad.lightPosts

        registeredRoad.let {
            binding.roadwayWidth.text = "${it.width.toInt()} M"
            binding.distanceBetweenLp.text = "${it.distanceEachLightPost.toInt()} M"
            binding.cable.text = it.cablePass
            binding.roadRegion.text = it.roadPath?.region.toString()
            binding.count.text = "${it.lightPostCounts}"
        }

    }

    private fun configRoadListRecyclerView() {
        recyclerView = binding.lightPostRecyclerView

        linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        recyclerView.apply {
            layoutManager = linearLayoutManager
            adapter = lightPostAdapter
        }

    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        (activity as AppCompatActivity).supportActionBar?.show()
    }
}