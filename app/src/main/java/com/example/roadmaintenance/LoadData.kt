package com.example.roadmaintenance

import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts

class LoadData(private val activity: MainActivity) {

    fun onCreateContents() {
        val fab: View = activity.findViewById(R.id.fab)
        val getPreviewImage =
            activity.registerForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { value ->
                Toast.makeText(activity.applicationContext, value.last().path,Toast.LENGTH_LONG).show()
            }
        fab.setOnClickListener {
            getPreviewImage.launch(null)
        }

    }
}
