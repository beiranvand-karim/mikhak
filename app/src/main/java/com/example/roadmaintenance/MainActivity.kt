package com.example.roadmaintenance

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.AttachedSurfaceControl
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.core.view.isVisible
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.roadmaintenance.databinding.ActivityMainBinding
import com.example.roadmaintenance.models.User
import com.example.roadmaintenance.repositories.UserRepository
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var userRepository: UserRepository
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var extras: Bundle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var user: User
    private lateinit var binding: ActivityMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

//
//
//        fetchDataFromDataBase()
//
//
        onCreateNavigationBar()

    }

    private fun fetchDataFromDataBase() {}

    private fun saveUserInfo() {
        userRepository.addUser(user)
    }

    private fun onCreateNavigationBar() {

        drawerLayout = binding.drawerLayout
        navView = binding.navView

        setSupportActionBar(binding.appBarMain.toolbar)

        navController = findNavController(R.id.nav_host)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.home_fragment
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        var profileName = binding.navView.getHeaderView(0).findViewById<TextView>(R.id.profile_name)

        if (userRepository.validateUser()) {
            profileName?.text = userRepository.getUser()!!.name
        } else {
            profileName?.text = user.name
        }

    }


    override fun onSupportNavigateUp(): Boolean {
        navController = findNavController(R.id.nav_host)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
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

}
