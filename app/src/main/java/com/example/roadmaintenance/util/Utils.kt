package com.example.roadmaintenance.util

import android.os.Build
import java.sql.Time
import java.time.LocalDate
import java.util.*

fun getDate(): String =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        java.sql.Date.valueOf(LocalDate.now().toString()).toString()
    else
        java.sql.Date(Date().time).toString()


fun getTime(): String = Time(Date().time).toString()