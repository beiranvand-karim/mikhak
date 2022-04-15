package com.example.roadmaintenance.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.roadmaintenance.R
import com.example.roadmaintenance.network.NetworkConnection
import com.google.android.material.snackbar.Snackbar

class HomeFragment : Fragment() {


    private var requestPermissionLauncher: ActivityResultLauncher<String>? = null
    private var getFileDataLauncher: ActivityResultLauncher<Array<String>>? = null
    private val permission: String = android.Manifest.permission.READ_EXTERNAL_STORAGE
    private lateinit var networkConnection: NetworkConnection

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        networkConnection = NetworkConnection(requireContext())
        networkConnection.observe(requireActivity()) {
            if (it) {
                Snackbar.make(view, "you are back online", Snackbar.LENGTH_SHORT).show()
                fetchData()
            } else {
                showAlertDialog()
            }
        }

        return view
    }

    private fun fetchData() {
        println("you are connected")
    }

    private fun showAlertDialog() {
        AlertDialog
            .Builder(context)
            .setTitle("No data connection")
            .setMessage("Consider turning on mobile data or turning on Wi-Fi")
            .setCancelable(false)
            .setNegativeButton("Ok", DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
            })
            .create()
            .show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fab: View = view.findViewById(R.id.fab)

        getFileDataLauncher =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { value: Uri? ->
                val inputStream = value?.let { activity?.contentResolver?.openInputStream(it) }
            }
        requestPermissionLauncher =
            this.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) getFileDataLauncher?.launch(null)
            }

        fab.setOnClickListener {
            requestPermissionLauncher?.launch(permission)
        }
    }

}
