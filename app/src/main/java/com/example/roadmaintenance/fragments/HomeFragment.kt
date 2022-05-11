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
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.roadmaintenance.MainActivity
import com.example.roadmaintenance.R
import com.example.roadmaintenance.adapter.PathListAdapter
import com.example.roadmaintenance.databinding.FragmentHomeBinding
import com.example.roadmaintenance.services.FileCache
import com.example.roadmaintenance.models.Pathway
import com.example.roadmaintenance.viewmodels.SharedViewModel

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
        FileCache(requireContext())
    }
    private val sharedViewModel: SharedViewModel by activityViewModels()

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
            if ((activity as MainActivity).isInternetAvailable)
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

        recyclerView?.run {
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
        sharedViewModel.pathways.observe(requireActivity()) {
            Log.i("Fetch home fragment", it?.body()?.size.toString())
            pathList = it?.body()
            pathListAdapter?.setPathList(pathList?.toMutableList())
        }
        sharedViewModel.sendResponse.observe(requireActivity()) {
            it.let {
                if (it.isSuccessful)
                    updateData()
            }
        }
    }

    private fun updateData() {
        sharedViewModel.getPathways()
        homeLayout.isRefreshing = false
        pathListAdapter?.setPathList(pathList?.toMutableList())
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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

}

