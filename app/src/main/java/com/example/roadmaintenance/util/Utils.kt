package com.example.roadmaintenance.util

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.view.Window
import com.example.roadmaintenance.R
import java.sql.Time
import java.time.LocalDate
import java.util.*

fun getDate(): String =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        java.sql.Date.valueOf(LocalDate.now().toString()).toString()
    else
        java.sql.Date(Date().time).toString()


fun getTime(): String = Time(Date().time).toString()

fun createProgressDialog(context: Context): Dialog {
    val progress = Dialog(context)
    progress.apply {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_progress_bar)
        window?.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))
    }
    return progress
}