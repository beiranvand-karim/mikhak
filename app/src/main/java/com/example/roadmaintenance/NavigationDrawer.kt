package com.example.roadmaintenance

import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class NavigationDrawer(private val activity: AppCompatActivity) {

    var toggle: ActionBarDrawerToggle? = null
        get() = field
        private set

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    fun onCreateNavigationBar() {

        drawerLayout = activity.findViewById(R.id.drawer_layout)
        navView = activity.findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(activity, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle!!)
        toggle!!.syncState()

        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener()
        {
            when (it.itemId) {
                R.id.open_action -> Toast.makeText(
                    activity.applicationContext,
                    "open",
                    Toast.LENGTH_SHORT
                )
                    .show()
                R.id.setting_action -> Toast.makeText(
                    activity.applicationContext,
                    "settings",
                    Toast.LENGTH_SHORT
                ).show()
                R.id.logout_action -> Toast.makeText(
                    activity.applicationContext,
                    "logout",
                    Toast.LENGTH_SHORT
                ).show()
            }
            true
        }
    }
}