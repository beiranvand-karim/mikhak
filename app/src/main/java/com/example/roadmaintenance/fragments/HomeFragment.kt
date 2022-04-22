package com.example.roadmaintenance.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityOptionsCompat
import androidx.core.net.toUri
import com.example.roadmaintenance.R
import com.example.roadmaintenance.api.RequestManager
import com.example.roadmaintenance.api.ServiceBuilder
import com.example.roadmaintenance.fileManager.FileCache
import com.example.roadmaintenance.models.Path
import com.example.roadmaintenance.network.NetworkConnection
import com.google.android.material.snackbar.Snackbar
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Multipart
import java.io.File

class HomeFragment : Fragment() {


    private var requestPermissionLauncher: ActivityResultLauncher<String>? = null
    private var getFileDataLauncher: ActivityResultLauncher<Array<String>>? = null
    private val permission: String = android.Manifest.permission.READ_EXTERNAL_STORAGE
    private lateinit var networkConnection: NetworkConnection
    private var alertDialog: AlertDialog? = null
    private var isInternetAvailable: Boolean = false

    private val fileCache: FileCache by lazy {
        FileCache(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        createAlertDialog()

        networkConnection = NetworkConnection(requireContext())
        networkConnection.observe(requireActivity()) {
            isInternetAvailable = it
            if (it) {
                Snackbar.make(view, "you are back online", Snackbar.LENGTH_SHORT)
                    .show()
                fetchData()
            } else {
                alertDialog?.show()
            }
        }

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fab: View = view.findViewById(R.id.fab)

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

    private fun fetchData() {
        val request = ServiceBuilder.buildService(RequestManager::class.java)
        val call = request.getUsers()

        call.enqueue(object : Callback<List<Path>> {
            override fun onResponse(call: Call<List<Path>>, response: Response<List<Path>>) {
                if (response.isSuccessful) {
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
}

