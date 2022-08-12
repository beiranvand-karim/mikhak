package com.example.roadmaintenance

import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.roadmaintenance.databinding.ActivityMainBinding
import com.example.roadmaintenance.databinding.ContentMainBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navDrawerView: NavigationView
    private var _mainBinding: ActivityMainBinding? = null
    private val mainBinding get() = _mainBinding!!
    private lateinit var contentBinding: ContentMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        setDarkMode()

        contentBinding = mainBinding.contentMain

        onCreateNavigationDrawer()
    }

    private fun setDarkMode() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }

    private fun configActionBar() {

        setSupportActionBar(mainBinding.contentMain.toolbar)
        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }
        navController = findNavController(R.id.nav_host)
        setupActionBarWithNavController(navController)
    }

    private fun onCreateNavigationDrawer() {

        drawerLayout = mainBinding.drawerLayout
        navDrawerView = mainBinding.navView

        configActionBar()

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment,
                R.id.lightPostFragment,
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)

        var profileName =
            mainBinding.navView.getHeaderView(0).findViewById<TextView>(R.id.profile_name)

        navDrawerView.setNavigationItemSelectedListener()
        {
            when (it.itemId) {
                R.id.open_action -> {
                    drawerLayout.close()
                    findViewById<FloatingActionButton>(R.id.fab).callOnClick()
                }
                R.id.home_action -> {
                    if (navController.currentDestination != navController.findDestination(R.id.homeFragment))
                        navController.popBackStack(R.id.homeFragment, false, true)
                    drawerLayout.close()
                }

                R.id.logout_action -> {
                    logout()
                    drawerLayout.close()
                }
            }
            true
        }
    }

    private fun logout() {
        findNavController(R.id.nav_host).navigate(R.id.action_homeFragment_to_loginFragment)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onSupportNavigateUp(): Boolean {
        navController = findNavController(R.id.nav_host)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onDestroy() {
        super.onDestroy()
        _mainBinding = null
    }
}
