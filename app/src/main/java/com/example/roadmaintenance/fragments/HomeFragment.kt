package com.example.roadmaintenance.fragments

import android.content.ContentResolver
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.roadmaintenance.IO.FileManager
import com.example.roadmaintenance.MainActivity
import com.example.roadmaintenance.R
import com.example.roadmaintenance.adapter.RoadListAdapter
import com.example.roadmaintenance.databinding.FragmentHomeBinding
import com.example.roadmaintenance.models.RegisteredRoad
import com.example.roadmaintenance.network.NetworkConnection
import com.example.roadmaintenance.util.Results
import com.example.roadmaintenance.viewmodels.RoadViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var homeLayout: SwipeRefreshLayout
    private var roadList: List<RegisteredRoad>? = null
    private lateinit var recyclerView: RecyclerView
    private var roadListAdapter: RoadListAdapter? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var navController: NavController
    private var requestPermissionLauncher: ActivityResultLauncher<String>? = null
    private var getFileDataLauncher: ActivityResultLauncher<Array<String>>? = null
    private val permission: String = android.Manifest.permission.READ_EXTERNAL_STORAGE
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val fileManager: FileManager by lazy {
        FileManager(requireContext().applicationContext)
    }
    private val roadViewModel: RoadViewModel by activityViewModels()
    private val networkConnection: NetworkConnection by lazy { NetworkConnection(requireContext().applicationContext) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        (activity as MainActivity).supportActionBar?.show()

        navController = view.findNavController()

        configRoadListRecyclerView()

        configSwipeToRefresh()

        subscribeObservers()

        configSelectFileLauncher()
    }

    private fun configSelectFileLauncher() {

        val fab: View = binding.fab

        getFileDataLauncher =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { value: Uri? ->
                value?.let {
                    if (isUriXMLSheet(it)) {
                        val file = fileManager.copyFromSource(it)
                        roadViewModel.uploadFile(file)
                    }
                }

            }
        requestPermissionLauncher =
            this.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) getFileDataLauncher?.launch(
                    arrayOf("*/*"),
                    ActivityOptionsCompat.makeBasic()
                )
            }

        fab.setOnClickListener {
            if (NetworkConnection.IsInternetAvailable)
                requestPermissionLauncher?.launch(permission)
            else
                roadViewModel.returnOfflineError()
        }

    }

    private fun isUriXMLSheet(uri: Uri): Boolean {
        return if (uri.toString().endsWith(".xslx")) true
        else {
            var contentResolver: ContentResolver
            var checkUri = false
            activity?.let { fragmentActivity ->
                contentResolver = fragmentActivity.contentResolver
                checkUri =
                    contentResolver.getStreamTypes(uri, "*/*")
                        ?.last()
                        .toString()
                        .endsWith(".sheet")
            }
            checkUri
        }
    }

    private fun configRoadListRecyclerView() {

        recyclerView = binding.recyclerView

        linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)


        roadListAdapter = if (roadList.isNullOrEmpty()) RoadListAdapter()
        else RoadListAdapter(roadList!!)

        recyclerView = binding.recyclerView

        recyclerView.run {
            layoutManager = linearLayoutManager
            adapter = roadListAdapter
        }
    }

    private fun configSwipeToRefresh() {
        homeLayout = binding.homeLayout
        homeLayout.setColorSchemeColors(
            ContextCompat.getColor(requireContext(), R.color.primary),
            ContextCompat.getColor(requireContext(), R.color.primary_dark)
        )
        homeLayout.setOnRefreshListener {
            updateData()
        }

    }

    private fun subscribeObservers() {

        networkConnection.onActive()

        lifecycleScope.launch {
            roadViewModel.resultState.collectLatest {
                when (it.status) {
                    Results.Status.LOADING -> homeLayout.isRefreshing = true
                    Results.Status.SUCCESS -> homeLayout.isRefreshing = false
                    Results.Status.UPLOAD_FILE_SUCCESS -> Toast.makeText(
                        requireContext(),
                        "File uploaded successfully",
                        Toast.LENGTH_LONG
                    ).show()
                    else -> showDialog(it)
                }
            }
        }

        lifecycleScope.launch {
            networkConnection.notifyValidNetwork.collectLatest {
                if (NetworkConnection.IsInternetAvailable != it) {
                    NetworkConnection.IsInternetAvailable = it
                    if (it) {
                        Snackbar.make(
                            requireView(),
                            "you are back online",
                            Snackbar.LENGTH_SHORT
                        )
                            .show()
                        updateData()
                    }
                }
            }
        }

        val loadRoads: () -> Unit = {
            roadListAdapter?.let { roadListAdapter ->
                roadListAdapter.roadList = roadList!!
                roadListAdapter.notifyDataSetChanged()
                showRecyclerView()
            }
        }

        roadList.apply {
            if (this.isNullOrEmpty()) {
                roadViewModel.roads.observe(viewLifecycleOwner) {
                    roadList = it
                    doIfRoadListValid(loadRoads)
                }
            } else {
                doIfRoadListValid(loadRoads)
            }

        }
    }

    private fun updateData() {
        networkConnection.onActive()
        roadViewModel.refreshRoads()
        doIfRoadListValid {
            roadListAdapter?.let {
                it.roadList = roadList!!
                it.notifyDataSetChanged()
            }
            showRecyclerView()
        }
        homeLayout.isRefreshing = false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.refresh -> {
                homeLayout.isRefreshing = true
                updateData()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun doIfRoadListValid(doTask: () -> Unit) {
        roadList.takeUnless {
            it.isNullOrEmpty()
        }?.apply {
            doTask()
        }
    }

    private fun showRecyclerView() {
        binding.noDataInclude.noDataLayout.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
    }

    private fun showDialog(results: Results) {
        MaterialAlertDialogBuilder(requireContext())
            .apply {
                setTitle(results.status.name)
                setMessage(results.message)
                setIcon(R.drawable.ic_warning)
                setNegativeButton(R.string.close){dialog,_ ->
                    dialog.cancel()
                }
                show()
            }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

