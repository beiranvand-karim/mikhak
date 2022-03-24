package com.example.roadmaintenance

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.example.roadmaintenance.models.User
import com.example.roadmaintenance.repository.UserRepository
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var loadData: LoadData
    private lateinit var userRepository: UserRepository
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var extras: Bundle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var user: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        onCreateNavigationBar()

        loadData = LoadData(this)
        loadData.onCreateContents()
    }

    fun saveUserInfo() {
        userRepository.addUser(user)
    }

    fun onCreateNavigationBar() {

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener()
        {
            when (it.itemId) {
                R.id.open_action -> Toast.makeText(
                    applicationContext,
                    "open",
                    Toast.LENGTH_SHORT
                )
                    .show()
                R.id.setting_action -> Toast.makeText(
                    applicationContext,
                    "settings",
                    Toast.LENGTH_SHORT
                ).show()
                R.id.logout_action -> logout()
            }
            true
        }

        var navHeader = navView.inflateHeaderView(R.layout.nav_header)
        var profileName = navHeader.findViewById<TextView>(R.id.profile_name)
        if (userRepository.validateUser()) {
            profileName.text = userRepository.getUser()!!.name
        } else {
            profileName.text = user.name
        }
    }

    fun logout() {
        if (userRepository.validateUser()) {
            userRepository.deleteUser()
            Toast.makeText(applicationContext, "you logged out", Toast.LENGTH_LONG).show()
        }
        var loginIntent = Intent(applicationContext, LoginPage::class.java)
        startActivity(loginIntent)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) return true
        return super.onOptionsItemSelected(item)
    }
}
