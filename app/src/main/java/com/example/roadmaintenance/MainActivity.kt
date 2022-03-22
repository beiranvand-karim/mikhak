package com.example.roadmaintenance

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem

class MainActivity : AppCompatActivity() {

    private lateinit var navDrawer: NavigationDrawer
    private lateinit var loadData: LoadData

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        navDrawer = NavigationDrawer(this)
        navDrawer.onCreateNavigationBar()

        val fab = LoadData(this)
        fab.onCreateContents()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (navDrawer.toggle!!.onOptionsItemSelected(item)) return true
        return super.onOptionsItemSelected(item)
    }


}