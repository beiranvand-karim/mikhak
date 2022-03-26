package com.example.roadmaintenance

import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.drawerlayout.widget.DrawerLayout
import com.example.roadmaintenance.models.LightPostSideEnum
import com.example.roadmaintenance.models.User
import com.example.roadmaintenance.repositories.DataRepository
import com.example.roadmaintenance.repositories.UserRepository
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var floatingButton: FloatingButton
    private lateinit var userRepository: UserRepository
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var extras: Bundle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var user: User
    private lateinit var dataRepository: DataRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sharedPreferences = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE)
        userRepository = UserRepository(sharedPreferences)

        dataRepository = DataRepository(
            openOrCreateDatabase(
                TRANSPORTATION_DATABASE,
                MODE_PRIVATE, null
            )
        )

        fetchDataFromDataBase()

        val dataBase = openOrCreateDatabase(TRANSPORTATION_DATABASE, MODE_PRIVATE, null)
        dataRepository = DataRepository(dataBase)

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

        floatingButton = FloatingButton(this,dataRepository)
        floatingButton.onCreateContents()
    }

    fun fetchDataFromDataBase() {
        var roadView = findViewById<TextView>(R.id.road_data)
        if (dataRepository.checkDataBase()) {
            var data = dataRepository.retrieveAllData()
            var lightPostSideEnum =
                if (data!!.lightPost.electricalPostSides == LightPostSideEnum.ONE_SIDE) "One Side"
                else "Two side"

            roadView.setText(
                "location : ${data!!.location} \n" + "width : ${data!!.width} KM"
                        + " \n distance between each light post : ${data.distanceEachLightPost} M"
                        + "\n \n light posts \n \n " + "light post height : ${data.lightPost.height} M"
                        + "\n light post power : ${data.lightPost.power} W"
                        + "\n light production type : ${data.lightPost.lightProductionType}"
                        + "\n light post sides : ${lightPostSideEnum}"
            )
        } else {
            roadView.setText("nothing yet")
        }
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
                    "Open files",
                    Toast.LENGTH_SHORT
                ).show()
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
        finish()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (toggle.onOptionsItemSelected(item)) return true
        return super.onOptionsItemSelected(item)
    }
}
