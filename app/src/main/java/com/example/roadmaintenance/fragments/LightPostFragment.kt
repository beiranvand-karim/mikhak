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
import com.example.roadmaintenance.RESTORE_ROADWAY
import com.example.roadmaintenance.adapter.LightPostAdapter
import com.example.roadmaintenance.databinding.FragmentLightPostBinding
import com.example.roadmaintenance.models.LightPost
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
    private var lightPosts: List<LightPost>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLightPostBinding.inflate(inflater, container, false)

        registeredRoad = args.selectedRoad
//        lightPosts = roadViewModel.
        configRoadListRecyclerView()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configHeader(view)
        setData()
    }

    private fun configHeader(view: View) {
        navController = view.findNavController()
        binding.toolbar.setupWithNavController(navController)
        showRoadOnMap = binding.showRoadOnMap
    }

    private fun setData() {
        registeredRoad.let {
            binding.roadwayWidth.text = "${it.width.toInt()} M"
            binding.distanceBetweenLp.text = "${it.distanceEachLightPost.toInt()} M"
            binding.cable.text = it.cablePass
            binding.roadRegion.text = it.roadPath?.region.toString()
            binding.count.text = "${it._lightPosts.size}"
        }
    }

    private fun configRoadListRecyclerView() {
        recyclerView = binding.lightPostRecyclerView

        lightPostAdapter.lightPostList = registeredRoad._lightPosts

        linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        recyclerView.apply {
            layoutManager = linearLayoutManager
            adapter = lightPostAdapter
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(RESTORE_ROADWAY, registeredRoad)
        super.onSaveInstanceState(outState)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            registeredRoad = it.getParcelable<RegisteredRoad>(RESTORE_ROADWAY)!!
            setData()
            lightPostAdapter.lightPostList = registeredRoad._lightPosts.toList()
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as AppCompatActivity).supportActionBar?.hide()
    }

    override fun onDestroy() {
        super.onDestroy()
        (activity as AppCompatActivity).supportActionBar?.show()
        _binding = null
    }

}