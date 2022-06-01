package com.example.roadmaintenance.fragments

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.*
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.roadmaintenance.MainActivity
import com.example.roadmaintenance.R
import com.example.roadmaintenance.RESTORE_PATHWAY_LIST
import com.example.roadmaintenance.SEND_PATHWAY_LIST
import com.example.roadmaintenance.adapter.PathListAdapter
import com.example.roadmaintenance.databinding.FragmentHomeBinding
import com.example.roadmaintenance.services.FileCache
import com.example.roadmaintenance.models.Pathway
import com.example.roadmaintenance.network.NetworkConnection
import com.example.roadmaintenance.viewmodels.HomeViewModel
import com.example.roadmaintenance.viewmodels.SharedViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {


    private lateinit var homeLayout: SwipeRefreshLayout
    private var pathList: List<Pathway>? = null
    private lateinit var recyclerView: RecyclerView
    private var pathListAdapter: PathListAdapter? = null
    private lateinit var linearLayoutManager: LinearLayoutManager
    private lateinit var navController: NavController
    private var alertDialog: AlertDialog? = null
    private var requestPermissionLauncher: ActivityResultLauncher<String>? = null
    private var getFileDataLauncher: ActivityResultLauncher<Array<String>>? = null
    private val permission: String = android.Manifest.permission.READ_EXTERNAL_STORAGE

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val fileCache: FileCache by lazy {
        FileCache(requireContext().applicationContext)
    }
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setHasOptionsMenu(true)

        alertDialog = (activity as MainActivity).alertDialog

        navController = view.findNavController()

        configPathListRecyclerView()

        configSwipeToRefresh()

        configRequestsObservers()

        configSelectFileLauncher()
    }

    private fun configSelectFileLauncher() {

        val fab: View = binding.fab

        getFileDataLauncher =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { value: Uri? ->
                value?.let {
                    if (it.toString().endsWith(".xlsx")) {
                        val file = fileCache.copyFromSource(it)
                        sharedViewModel.uploadFile(file)
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
            else alertDialog?.show()
        }

    }

    private fun configPathListRecyclerView() {
        recyclerView = binding.recyclerView

        linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        pathListAdapter = PathListAdapter(pathList?.toMutableList())
        recyclerView = binding.recyclerView

        recyclerView.run {
            layoutManager = linearLayoutManager
            adapter = pathListAdapter
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

    private fun configRequestsObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            homeLayout.isRefreshing = true
            sharedViewModel.pathways.collectLatest {
                Log.i("Fetch home fragment", it.body()?.size.toString())
                it.body()?.let { responseBody ->
                    homeViewModel.getRoutesData(responseBody)
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            homeViewModel.shapedPath.collectLatest {
                it?.let {
                    pathList = it
                    onFetchPathways()
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.isUploadFileSuccess.collectLatest {
                if (it)
                    updateData()
            }
        }
    }

    private fun onFetchPathways() {
        fileCache.removeAll()
        pathList?.let {
            pathListAdapter?.setPathList(it.toMutableList())
            val bundle = Bundle()
            bundle.putParcelableArray(SEND_PATHWAY_LIST, it.toTypedArray())
            setFragmentResult(SEND_PATHWAY_LIST, bundle)
        }
        homeLayout.isRefreshing = false
    }

    private fun updateData() {
        sharedViewModel.getPathways()
        pathListAdapter?.setPathList(pathList?.toMutableList())
        fileCache.removeAll()
        homeLayout.isRefreshing = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        pathList?.let {
            outState.putParcelableArray(RESTORE_PATHWAY_LIST, it.toTypedArray())
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let { bundle ->
            val pathArray = bundle.getParcelableArray(RESTORE_PATHWAY_LIST)
            pathArray?.let {
                pathList = it.toMutableList() as MutableList<Pathway>
                pathListAdapter?.let { pathListAdapter ->
                    pathListAdapter.setPathList(pathList?.toMutableList())
                }
            }
        }
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

