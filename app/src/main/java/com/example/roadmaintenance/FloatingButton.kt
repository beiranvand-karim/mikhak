package com.example.roadmaintenance

import android.content.pm.PackageManager
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.roadmaintenance.models.Road
import com.example.roadmaintenance.processor.CSVProcessor
import com.example.roadmaintenance.repositories.DataRepository

class FloatingButton(
    private val activity: MainActivity,
    private val dataRepository: DataRepository
) {

    private lateinit var csvProcessor: CSVProcessor


    var road: Road? = null

    fun onCreateContents() {
        val fab: View = activity.findViewById(R.id.fab)
        val getFilesData =
            activity.registerForActivityResult(ActivityResultContracts.OpenDocument()) { value ->
                val inputStream = activity.contentResolver.openInputStream(value)
                csvProcessor = CSVProcessor(inputStream)
                road = csvProcessor.readCsv()
                dataRepository.insertData(road!!)
                activity.fetchDataFromDataBase()
            }
        fab.setOnClickListener {
            if (checkForPermissions()) {
                getFilesData.launch(null)
            } else {
                requestForPermissions()
            }
        }

    }

    fun checkForPermissions(): Boolean {
        if (activity.checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED
        ) return true
        return false
    }

    fun requestForPermissions() {
        var permission = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        activity.requestPermissions(permission, 1)
        if (checkForPermissions()) onCreateContents()
        else {
            Toast.makeText(activity.applicationContext, "Please allow access", Toast.LENGTH_LONG)
                .show()
        }
    }
}
