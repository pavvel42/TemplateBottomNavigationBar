package com.example.templatebottomnavmenu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.leinardi.android.speeddial.SpeedDialView
import com.squareup.picasso.Picasso

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private val TAG = MainActivity::class.java.simpleName
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var floatingActionButton: SpeedDialView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navBottomView: BottomNavigationView
    private lateinit var avatar_profile: ImageView
    private lateinit var user_name: TextView
    private lateinit var user_emial: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buildNavigationAndView()
        initialize()
    }

    // Icon Settings in ActionBar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    // Icon Settings in ActionBar
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.settings) {
            Log.d(TAG, "Change fragment")
            val navController = findNavController(R.id.nav_host_fragment)
            val idCurrentDestinationFragment = navController.currentDestination?.id
            val idSettingsFragment = R.id.navigation_settings
            Log.d(TAG, "Check ID Fragment: $idCurrentDestinationFragment ?== $idSettingsFragment")
            if(idCurrentDestinationFragment == idSettingsFragment){
                navController.popBackStack()
            }
            navController.navigate(R.id.navigation_settings)
            prepareSettingsView()
        }
        return super.onOptionsItemSelected(item)
    }

    // Navigation Drawer Menu
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.logout -> {
                signOut()
            }
            else -> {
                Toast.makeText(applicationContext, "Other Clicked", Toast.LENGTH_SHORT).show()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun prepareSettingsView(){
        navBottomView.visibility = View.GONE
        floatingActionButton.clearActionItems()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Log.d(TAG, "onBackPressed")
        navBottomView.visibility = View.VISIBLE
        speedDialView()
    }

    private fun initialize() {
        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = Firebase.auth //ktx
        // [END initialize_auth]
        Picasso.get().load(auth.currentUser?.photoUrl.toString()).into(avatar_profile)
        user_name.text = auth.currentUser?.displayName.toString()
        user_emial.text = (auth.currentUser?.email.toString())
    }

    private fun speedDialView() {
        floatingActionButton.inflate(R.menu.floating_action_button_menu)
        floatingActionButton.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.action_show_home -> {
                    Toast.makeText(applicationContext, R.string.title_home, Toast.LENGTH_SHORT)
                        .show()
                    floatingActionButton.close()
                    return@OnActionSelectedListener true
                }
                R.id.action_show_notifications -> {
                    Toast.makeText(
                        applicationContext,
                        R.string.title_notifications,
                        Toast.LENGTH_SHORT
                    ).show()
                    floatingActionButton.close()
                    return@OnActionSelectedListener true
                }
                R.id.action_show_dashboard -> {
                    Toast.makeText(applicationContext, R.string.title_dashboard, Toast.LENGTH_SHORT)
                        .show()
                    floatingActionButton.close()
                    return@OnActionSelectedListener true
                }
            }
            false
        })
    }

    private fun buildNavigationAndView() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        navBottomView = findViewById(R.id.navigation_bottom_view)

        val navControllerBottom = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfigurationBottom = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_notifications,
                R.id.navigation_settings
            )
        )
        setupActionBarWithNavController(navControllerBottom, appBarConfigurationBottom)
        navBottomView.setupWithNavController(navControllerBottom)

        drawerLayout = findViewById(R.id.drawer_layout)
        val navDrawer: NavigationView = findViewById(R.id.navigation_drawer)

        navDrawer.setNavigationItemSelectedListener(this)
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        floatingActionButton = findViewById(R.id.floatingActionButton)
        speedDialView()

        val headerView = navDrawer.getHeaderView(0) //<View>
        avatar_profile = headerView.findViewById(R.id.avatar_profile)
        user_name = headerView.findViewById(R.id.user_name)
        user_emial = headerView.findViewById(R.id.user_emial)
    }

    private fun signOut() {
        auth.signOut()
        // [START config_signOut]
        // Configure Google Sign Out
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        googleSignInClient.signOut().addOnCompleteListener(this, OnCompleteListener {
            finish()
            startActivity(Intent(this@MainActivity, GoogleSignInActivity::class.java))
            Log.d(TAG, "logout:success")
        })
        // [END config_signOut]
    }
}
