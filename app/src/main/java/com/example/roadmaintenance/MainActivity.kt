package com.example.roadmaintenance

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.roadmaintenance.databinding.ActivityMainBinding
import com.example.roadmaintenance.databinding.ContentMainBinding
import com.example.roadmaintenance.viewmodels.UserViewModel
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
    private var isDoubleBackPressed = false
    private val userViewModel: UserViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        contentBinding = mainBinding.contentMain
        onCreateNavigationDrawer()
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

        navDrawerView.setNavigationItemSelectedListener()
        {
            when (it.itemId) {
                R.id.open_action -> {
                    drawerLayout.close()
                    findViewById<FloatingActionButton>(R.id.fileFab).callOnClick()
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

    override fun onBackPressed() {
        if (navController.currentDestination == navController.findDestination(R.id.homeFragment)) {
            showExitMessage()
            if (isDoubleBackPressed)
                stopApplication()
        } else
            super.onBackPressed()

        isDoubleBackPressed = !isDoubleBackPressed
    }

    private fun showExitMessage() {
        Toast.makeText(
            applicationContext,
            "Press again to exit",
            Toast.LENGTH_SHORT
        ).show()
    }


    private fun stopApplication() {
        finish()
    }

    private fun logout() {
        userViewModel.logOut()
        findNavController(R.id.nav_host).navigate(R.id.action_homeFragment_to_checkFragment)
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
