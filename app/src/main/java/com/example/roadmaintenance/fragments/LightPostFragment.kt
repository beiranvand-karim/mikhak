package com.example.roadmaintenance.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.roadmaintenance.RESTORE_PATHWAY
import com.example.roadmaintenance.adapter.LightPostAdapter
import com.example.roadmaintenance.databinding.FragmentLightPostBinding
import com.example.roadmaintenance.models.Pathway
import com.google.android.material.floatingactionbutton.FloatingActionButton


class LightPostFragment : Fragment() {

    private val args by navArgs<LightPostFragmentArgs>()

    private lateinit var pathway: Pathway

    private lateinit var showPathOnMap: FloatingActionButton
    private var _binding: FragmentLightPostBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var navController: NavController

    private val lightPostAdapter: LightPostAdapter by lazy { LightPostAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLightPostBinding.inflate(inflater, container, false)

        pathway = args.selectedPathway

        configPathListRecyclerView()

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
        showPathOnMap = binding.showPathOnMap
    }

    private fun setData() {
        pathway.let {
            binding.pathWidth.text = "${it.width.toInt()} M"
            binding.distanceBetweenLp.text = "${it.distanceEachLightPost.toInt()} M"
            binding.cable.text = it.cablePass
            binding.pathRegion.text = it.routeShape?.region.toString()
            binding.count.text = "${it.lightPosts.size}"
        }
    }

    private fun configPathListRecyclerView() {
        recyclerView = binding.lightPostRecyclerView

        lightPostAdapter.lightPostList = pathway.lightPosts

        linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        recyclerView.apply {
            layoutManager = linearLayoutManager
            adapter = lightPostAdapter
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putParcelable(RESTORE_PATHWAY, pathway)
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let {
            pathway = it.getParcelable<Pathway>(RESTORE_PATHWAY)!!
            setData()
            lightPostAdapter.lightPostList = pathway.lightPosts.toList()
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