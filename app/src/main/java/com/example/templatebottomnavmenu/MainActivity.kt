package com.example.templatebottomnavmenu

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.res.ResourcesCompat
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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.leinardi.android.speeddial.SpeedDialActionItem
import com.leinardi.android.speeddial.SpeedDialView
import com.squareup.picasso.Picasso
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.AppSettingsDialog
import pub.devrel.easypermissions.EasyPermissions

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, EasyPermissions.PermissionCallbacks {

    private val TAG = MainActivity::class.java.simpleName
    private val constraintLayout by lazy { findViewById<ConstraintLayout>(R.id.container) }
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var floatingActionButton: SpeedDialView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navBottomView: BottomNavigationView
    private lateinit var avatar_profile: ImageView
    private lateinit var user_name: TextView
    private lateinit var user_email: TextView
    private var snackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        buildNavigationAndView()
        initialize()
    }

    // Icon Settings Create - ActionBar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        val itemMenu = menu?.findItem(R.id.settings)
        val rootView: View = itemMenu?.actionView as View //FrameLayout Icon Settings
        rootView.setOnClickListener { onOptionsItemSelected(item = itemMenu); rootView.animateView() }
        return super.onCreateOptionsMenu(menu)
    }

    // Icon Settings Select - ActionBar
    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.settings -> {
            val navController = findNavController(R.id.nav_host_fragment)
            val idCurrentDestinationFragment = navController.currentDestination?.id
            val idSettingsFragment = R.id.navigation_settings
            Log.d(TAG, "Check ID Fragment: $idCurrentDestinationFragment ?== $idSettingsFragment")
            if(idCurrentDestinationFragment == idSettingsFragment){
                navController.popBackStack()
            }
            navController.navigate(R.id.navigation_settings)
            prepareSettingsView()
            true
        }
        else -> {
            // If we got here, the user's action was not recognized.
            // Invoke the superclass to handle it.
            super.onOptionsItemSelected(item)
        }
    }

    // Navigation Drawer Menu
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_permission -> { checkPermissions() }
            R.id.logout -> { signOut() }
            else -> {
                val nameActionItem = item.title
                showSnackbar(getString(R.string.write_action_for)+" $nameActionItem")
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun prepareSettingsView(){
        navBottomView.visibility = View.GONE
        floatingActionButton.clearActionItems()
        floatingActionButton.setMainFabClosedDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_keyboard_return_24, theme))
        floatingActionButton.mainFabClosedIconColor = ResourcesCompat.getColor(resources, R.color.colorWhite, theme)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        Log.d(TAG, "onBackPressed")
        navBottomView.visibility = View.VISIBLE
        floatingActionButton.clearActionItems()
        speedDialViewBuilder()
    }

    private fun initialize() {
        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = Firebase.auth //ktx
        // [END initialize_auth]
        Picasso.get().load(auth.currentUser?.photoUrl.toString()).into(avatar_profile)
        user_name.text = auth.currentUser?.displayName.toString()
        user_email.text = (auth.currentUser?.email.toString())
    }

    private fun speedDialViewInflate() {
        floatingActionButton.inflate(R.menu.floating_action_button_menu)
        floatingActionButton.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem ->
            when (actionItem.id) {
                R.id.action_show_home -> {
                    Toast.makeText(applicationContext, R.string.title_home, Toast.LENGTH_SHORT).show()
                    floatingActionButton.close()
                    return@OnActionSelectedListener true
                }
                R.id.action_show_notifications -> {
                    Toast.makeText(applicationContext, R.string.title_notifications, Toast.LENGTH_SHORT).show()
                    floatingActionButton.close()
                    return@OnActionSelectedListener true
                }
                R.id.action_show_dashboard -> {
                    Toast.makeText(applicationContext, R.string.title_dashboard, Toast.LENGTH_SHORT).show()
                    floatingActionButton.close()
                    return@OnActionSelectedListener true
                }
            }
            false
        })
    }

    private fun speedDialViewBuilder() {

        floatingActionButton.setOnChangeListener(object : SpeedDialView.OnChangeListener {
            override fun onMainActionSelected(): Boolean {
                Log.d(TAG, "Main action clicked!")
                val navController = findNavController(R.id.nav_host_fragment)
                val idCurrentDestinationFragment = navController.currentDestination?.id
                val idSettingsFragment = R.id.navigation_settings
                Log.d(TAG, "Check ID Fragment: $idCurrentDestinationFragment ?== $idSettingsFragment")
                if(idCurrentDestinationFragment == idSettingsFragment){
                    onBackPressed()
                }
                return false // True to keep the Speed Dial open
            }

            override fun onToggleChanged(isOpen: Boolean) {
                Log.d(TAG, "Speed dial toggle state changed. Open = $isOpen")
            }
        })

        floatingActionButton.setMainFabClosedDrawable(ResourcesCompat.getDrawable(resources, R.drawable.ic_baseline_add_24, theme))
        floatingActionButton.addActionItem(SpeedDialActionItem.Builder(R.id.fab_home, R.drawable.ic_baseline_home_24)
            .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorSecondaryVariant, theme))
            .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.colorWhite, theme))
            .setLabel(getString(R.string.title_home))
            .create())
        floatingActionButton.addActionItem(SpeedDialActionItem.Builder(R.id.fab_dashboard, R.drawable.ic_baseline_dashboard_24)
            .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorSecondaryVariant, theme))
            .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.colorWhite, theme))
            .setLabel(getString(R.string.title_dashboard))
            .create())
        floatingActionButton.addActionItem(SpeedDialActionItem.Builder(R.id.fab_notifications, R.drawable.ic_baseline_notifications_24)
            .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorSecondaryVariant, theme))
            .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.colorWhite, theme))
            .setLabel(getString(R.string.title_notifications))
            .create())

        floatingActionButton.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem -> when (actionItem.id) {
                R.id.fab_home -> {
                    floatingActionButton.replaceActionItem(SpeedDialActionItem.Builder(actionItem.id, R.drawable.ic_baseline_home_24)
                        .setFabBackgroundColor(ResourcesCompat.getColor(resources, R.color.colorWhite, theme))
                        .setFabImageTintColor(ResourcesCompat.getColor(resources, R.color.colorSecondaryVariant, theme))
                        .setLabel(getString(R.string.title_home))
                        .create(), 0)
                    //floatingActionButton.close() // To close the Speed Dial with animation
                    return@OnActionSelectedListener true // false will close it without animation
                }
                else -> {
                    val nameActionItem = actionItem.getLabel(this@MainActivity)
                    showSnackbar(getString(R.string.write_action_for)+" $nameActionItem")
                    floatingActionButton.close() // To close the Speed Dial with animation
                    return@OnActionSelectedListener true // false will close it without animation
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
        speedDialViewBuilder()

        val headerView = navDrawer.getHeaderView(0) //<View>
        avatar_profile = headerView.findViewById(R.id.avatar_profile)
        user_name = headerView.findViewById(R.id.user_name)
        user_email = headerView.findViewById(R.id.user_email)
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

    private fun View.animateView() {
        var r = RotateAnimation(0F, 360F, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        r.duration = 2.toLong() * 500
        r.repeatCount = 0
        startAnimation(r)
    }

    private fun showSnackbar(text: String) {
        snackbar = Snackbar.make(constraintLayout, text, Snackbar.LENGTH_SHORT)
        checkNotNull(snackbar).apply {
            setAction("Close") { dismiss() }
            show()
        }
    }

    @AfterPermissionGranted(Companion.RC_CAMERA_AND_LOCATION)
    private fun checkPermissions(){
        val perms = arrayOf<String>(Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
        if (EasyPermissions.hasPermissions(this, *perms)) {
            // Already have permission, do the thing
            showSnackbar(getString(R.string.already_have_permission))
        } else {
            // Do not have permissions, request them now
            EasyPermissions.requestPermissions(this, getString(R.string.example_permission),
                Companion.RC_CAMERA_AND_LOCATION, *perms);
        }
    }

    //override methods RequestPermissions
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
        Log.d(TAG, "PermissionsDenied")
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            AppSettingsDialog.Builder(this).build().show()
        }
    }

    override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
        Log.d(TAG, "PermissionsGranted")
    }

    companion object {
        private const val RC_CAMERA_AND_LOCATION = 123
    }
}
