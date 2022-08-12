package com.example.roadmaintenance.fragments

import android.app.AlertDialog
import android.content.ContentResolver
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
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
import com.example.roadmaintenance.MainActivity
import com.example.roadmaintenance.R
import com.example.roadmaintenance.RESTORE_PATHWAYS
import com.example.roadmaintenance.adapter.PathListAdapter
import com.example.roadmaintenance.databinding.FragmentHomeBinding
import com.example.roadmaintenance.models.Pathway
import com.example.roadmaintenance.network.NetworkConnection
import com.example.roadmaintenance.services.FileManager
import com.example.roadmaintenance.viewmodels.HomeViewModel
import com.example.roadmaintenance.viewmodels.SharedViewModel
import com.google.android.material.snackbar.Snackbar
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

    private val fileManager: FileManager by lazy {
        FileManager(requireContext().applicationContext)
    }
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val homeViewModel: HomeViewModel by activityViewModels()

    private val networkConnection: NetworkConnection by lazy { NetworkConnection(requireContext().applicationContext) }

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

        (activity as MainActivity).supportActionBar?.show()

        navController = view.findNavController()

        createAlertDialog()

        configPathListRecyclerView()

        configSwipeToRefresh()

        configRequestsObservers()

        configSelectFileLauncher()

        doIfPathListValid {
            showRecyclerView()
        }
    }

    private fun configSelectFileLauncher() {

        val fab: View = binding.fab

        getFileDataLauncher =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { value: Uri? ->
                value?.let {
                    if (checkUriIsXMLSheet(it)) {
                        val file = fileManager.copyFromSource(it)
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

    private fun checkUriIsXMLSheet(uri: Uri): Boolean {
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

    private fun configPathListRecyclerView() {

        recyclerView = binding.recyclerView

        linearLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)


        pathListAdapter = if (pathList.isNullOrEmpty()) PathListAdapter()
        else PathListAdapter(pathList!!)

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

        networkConnection.onActive()

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
                    } else {
                        alertDialog?.show()
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.pathways.collectLatest { response ->
                Log.i("Fetch home fragment", response.body()?.size.toString())
                homeLayout.isRefreshing = true
                response.body()?.let { responseBody ->
                    homeViewModel.getRoutesData(responseBody)

                    if (!responseBody.isNullOrEmpty()) {
                        homeViewModel.shapedPath.collectLatest { shapedPaths ->
                            shapedPaths?.let {
                                pathList = it
                                onFetchPathways()
                            }
                        }
                    } else
                        homeLayout.isRefreshing = false
                }
                homeLayout.isRefreshing = false
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            sharedViewModel.isUploadFileSuccess.collectLatest {
                if (it) updateData()
            }
        }
    }

    private fun updateData() {
        sharedViewModel.getPathways()
        onFetchPathways()
    }

    private fun onFetchPathways() {
        doIfPathListValid {
            pathListAdapter?.let {
                it.pathList = pathList!!
                it.notifyDataSetChanged()
            }
            showRecyclerView()
        }
        homeLayout.isRefreshing = false
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        doIfPathListValid {
            outState.putParcelableArray(RESTORE_PATHWAYS, pathList!!.toTypedArray())
        }
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)
        savedInstanceState?.let { bundle ->
            val pathArray = bundle.getParcelableArray(RESTORE_PATHWAYS)
            pathArray
                .takeUnless {
                    it.isNullOrEmpty()
                }?.apply {
                    showRecyclerView()
                    pathList = this.toList() as List<Pathway>
                    pathListAdapter?.let { pathListAdapter ->
                        pathListAdapter.pathList = pathList!!
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

    private fun doIfPathListValid(doTask: () -> Unit) {
        pathList.takeUnless {
            it.isNullOrEmpty()
        }?.apply {
            doTask()
        }
    }

    private fun showRecyclerView() {
        binding.noDataInclude.noDataLayout.visibility = View.GONE
        binding.recyclerView.visibility = View.VISIBLE
    }

    private fun createAlertDialog() {
        alertDialog = AlertDialog
            .Builder(requireContext())
            .setTitle("No data connection")
            .setMessage("Consider turning on mobile data or turning on Wi-Fi")
            .setCancelable(false)
            .setNegativeButton("Ok", DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
            })
            .create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

