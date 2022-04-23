package com.example.roadmaintenance

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.roadmaintenance.api.EndPoints
import com.example.roadmaintenance.api.RequestManager
import com.example.roadmaintenance.api.ServiceBuilder
import com.example.roadmaintenance.databinding.ActivityMainBinding
import com.example.roadmaintenance.databinding.ContentMainBinding
import com.example.roadmaintenance.fileManager.FileCache
import com.example.roadmaintenance.models.Path
import com.example.roadmaintenance.models.User
import com.example.roadmaintenance.network.NetworkConnection
import com.example.roadmaintenance.repositories.UserRepository
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.snackbar.Snackbar
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var userRepository: UserRepository
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var extras: Bundle
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navDrawerView: NavigationView
    private lateinit var navBottomView: BottomNavigationView
    private lateinit var user: User
    private lateinit var mainBinding: ActivityMainBinding
    private lateinit var contentBinding: ContentMainBinding
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private var requestPermissionLauncher: ActivityResultLauncher<String>? = null
    private var getFileDataLauncher: ActivityResultLauncher<Array<String>>? = null
    private val permission: String = android.Manifest.permission.READ_EXTERNAL_STORAGE
    private var alertDialog: AlertDialog? = null
    private var isInternetAvailable: Boolean = false
    private lateinit var swipeRefresh: SwipeRefreshLayout

    private val networkConnection: NetworkConnection by lazy { NetworkConnection(this) }
    private val fileCache: FileCache by lazy {
        FileCache(applicationContext)
    }
    private val requestManager: RequestManager by lazy {
        RequestManager()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)

        contentBinding = mainBinding.contentMain

        sharedPreferences = getSharedPreferences(USER_PREFERENCES, MODE_PRIVATE)
        userRepository = UserRepository(sharedPreferences)

        setSupportActionBar(mainBinding.contentMain.toolbar)
        supportActionBar?.apply {
            setHomeButtonEnabled(true)
            setDisplayHomeAsUpEnabled(true)
        }

        createAlertDialog()
        configSwipeToRefresh()
        onCreateNavigationDrawer()
        onCreateBottomNavigation()

        networkConnection.observe(this) {
            isInternetAvailable = it
            if (it) {
                Snackbar.make(mainBinding.root, "you are back online", Snackbar.LENGTH_SHORT)
                    .show()
                requestManager.fetchData()
            } else {
                alertDialog?.show()
            }
        }

        val fab: View = contentBinding.bottomAppBarLayout.fab

        getFileDataLauncher =
            registerForActivityResult(ActivityResultContracts.OpenDocument()) { value: Uri? ->
                value?.let {
                    if (it.toString().endsWith(".xlsx")) {
                        val file = fileCache.copyFromSource(it)
                        sendData(file)
                    }
                }

            }
        requestPermissionLauncher =
            this.registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
                if (isGranted) getFileDataLauncher?.launch(
                    arrayOf("*/*"),
                    ActivityOptionsCompat.makeBasic()
                )
            }

        fab.setOnClickListener {
            if (isInternetAvailable)
                requestPermissionLauncher?.launch(permission)
            else alertDialog?.show()
        }

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
    }

    private fun saveUserInfo() {
        userRepository.addUser(user)
    }

    private fun onCreateNavigationDrawer() {

        drawerLayout = mainBinding.drawerLayout
        navDrawerView = mainBinding.navView

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
    }

    private fun onCreateBottomNavigation() {
        navBottomView = contentBinding.bottomAppBarLayout.bottomNav

        navController = findNavController(R.id.nav_host)
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.home_navigation, R.id.map_navigation
            ), drawerLayout
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navBottomView.setupWithNavController(navController)

        navBottomView.apply {
            background = null
            menu.getItem(1).isEnabled = false
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

    private fun configSwipeToRefresh() {
        swipeRefresh = contentBinding.swipeRefresh
        swipeRefresh.setColorSchemeColors(
            ContextCompat.getColor(this, R.color.primary),
            ContextCompat.getColor(this, R.color.primary_dark)
        )
        swipeRefresh.setOnRefreshListener {
            updateData()
        }

    }

    private fun updateData() {
        requestManager.fetchData()
        swipeRefresh.isRefreshing = false
    }

    private fun createAlertDialog() {
        alertDialog = AlertDialog
            .Builder(this)
            .setTitle("No data connection")
            .setMessage("Consider turning on mobile data or turning on Wi-Fi")
            .setCancelable(false)
            .setNegativeButton("Ok", DialogInterface.OnClickListener { dialog, id ->
                dialog.cancel()
            })
            .create()
    }

    private fun sendData(file: File) {
        if (requestManager.uploadData(file))
            fileCache.removeAll()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.home_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.refresh -> {
                swipeRefresh.isRefreshing = true
                updateData()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        navController = findNavController(R.id.nav_host)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }
}
