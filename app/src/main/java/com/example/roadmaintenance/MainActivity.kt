package com.example.roadmaintenance

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.roadmaintenance.databinding.ActivityMainBinding
import com.example.roadmaintenance.databinding.ContentMainBinding
import com.example.roadmaintenance.fragments.HomeFragment
import com.example.roadmaintenance.models.User
import com.example.roadmaintenance.network.NetworkConnection
import com.example.roadmaintenance.repositories.UserRepository
import com.example.roadmaintenance.viewmodels.SharedViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var userRepository: UserRepository
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var extras: Bundle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navDrawerView: NavigationView
    private lateinit var user: User
    private var _mainBinding: ActivityMainBinding? = null
    private val mainBinding get() = _mainBinding!!
    private lateinit var contentBinding: ContentMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        contentBinding = mainBinding.contentMain

        sharedPreferences = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE)
        userRepository = UserRepository(sharedPreferences)

        if (!intent.extras!!.isEmpty) {
            extras = intent.extras!!
            user = User(
                extras.getInt(ID),
                extras.getString(USERNAME),
                extras.getString(PASSWORD)
            )
            val rememberMeValue = extras.getBoolean(REMEMBER_ME, false)
            if (rememberMeValue) saveUserInfo()
        }

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

    private fun saveUserInfo() {
        userRepository.addUser(user)
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

        if (userRepository.validateUser()) {
            profileName?.text = userRepository.getUser()!!.name
        } else {
            profileName?.text = user.name
        }

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

                R.id.logout_action -> logout()
            }
            true
        }
    }

    private fun logout() {
        if (userRepository.validateUser()) {
            userRepository.deleteUser()
            Toast.makeText(applicationContext, "you logged out", Toast.LENGTH_LONG).show()
        }
        var loginIntent = Intent(applicationContext, LoginPage::class.java)
        startActivity(loginIntent)
        finish()
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
