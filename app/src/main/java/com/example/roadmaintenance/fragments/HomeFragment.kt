package com.example.roadmaintenance.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.Px
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.roadmaintenance.MainActivity
import com.example.roadmaintenance.R
import com.example.roadmaintenance.api.RequestManager
import com.example.roadmaintenance.api.ServiceBuilder
import com.example.roadmaintenance.databinding.FragmentHomeBinding
import com.example.roadmaintenance.fileManager.FileCache
import com.example.roadmaintenance.models.Path
import com.example.roadmaintenance.network.NetworkConnection
import com.google.android.material.behavior.SwipeDismissBehavior
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class HomeFragment : Fragment() {


    private var requestPermissionLauncher: ActivityResultLauncher<String>? = null
    private var getFileDataLauncher: ActivityResultLauncher<Array<String>>? = null
    private val permission: String = android.Manifest.permission.READ_EXTERNAL_STORAGE
    private var alertDialog: AlertDialog? = null
    private var isInternetAvailable: Boolean = false

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var swipeRefresh: SwipeRefreshLayout

    private val networkConnection: NetworkConnection by lazy { NetworkConnection(requireContext()) }
    private val fileCache: FileCache by lazy {
        FileCache(requireContext())
    }

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

        createAlertDialog()
        configSwipeToRefresh()

        networkConnection.observe(requireActivity()) {
            isInternetAvailable = it
            if (it) {
                Snackbar.make(binding.root, "you are back online", Snackbar.LENGTH_SHORT)
                    .show()
                fetchData()
            } else {
                alertDialog?.show()
            }
        }

        val fab: View = binding.fab

        getFileDataLauncher =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { value: Uri? ->
                value?.let {
                    if (it.toString().endsWith(".xlsx")) {
                        val file = fileCache.copyFromSource(it)
                        sendData(file)
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
            if (isInternetAvailable)
                requestPermissionLauncher?.launch(permission)
            else alertDialog?.show()
        }
    }

    private fun configSwipeToRefresh() {
        swipeRefresh = binding.swipeRefresh
        swipeRefresh.setColorSchemeColors(
            ContextCompat.getColor(requireContext(), R.color.primary),
            ContextCompat.getColor(requireContext(), R.color.primary_dark)
        )
        swipeRefresh.setOnRefreshListener {
            updateData()
        }

    }

    private fun updateData() {
        fetchData()
        swipeRefresh.isRefreshing = false
    }

    private fun fetchData() {
        val request = ServiceBuilder.buildService(RequestManager::class.java)
        val call = request.getUsers()

        call.enqueue(object : Callback<List<Path>> {
            override fun onResponse(call: Call<List<Path>>, response: Response<List<Path>>) {
                if (response.isSuccessful) {
                    println("fetch data")
                } else {
                    Log.e("Fetch data", "Fetch Response is not successful")
                }
            }

            override fun onFailure(call: Call<List<Path>>, t: Throwable) {
                Log.e("Fetch data", "${t.message}")
                Log.e("Fetch data", "Fetch Request is not successful")
            }
        })
    }

    private fun createAlertDialog() {
        alertDialog = AlertDialog
            .Builder(context)
            .setTitle("No data connection")
            .setMessage("Consider turning on mobile data or turning on Wi-Fi")
            .setCancelable(false)
            .setNegativeButton("Ok", DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
            })
            .create()
    }


    private fun sendData(file: File) {
        val request = ServiceBuilder.buildService(RequestManager::class.java)

        val requestBody = RequestBody.create(null, file)
        val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
        val call = request.uploadFile(part)

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Log.d("Send-Data", "response is successful")
                    fileCache.removeAll()
                } else {
                    Log.e("Send-Data", response.headers().toString())
                    Log.e("Send-Data", "upload file response is not success full")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("Send-Data", "upload file request is not correct")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.refresh -> {
                swipeRefresh.isRefreshing = true
                updateData()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

}

