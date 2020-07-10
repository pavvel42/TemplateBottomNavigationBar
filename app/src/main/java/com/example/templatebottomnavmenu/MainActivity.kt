package com.example.templatebottomnavmenu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.leinardi.android.speeddial.SpeedDialView

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var floatingActionButton: SpeedDialView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initialize()
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications, R.id.navigation_account))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.toolbar_menu, menu)
//        menu?.findItem(R.id.avatar)?.setIcon(R.drawable.ic_google_logo) //avatar
//        var icon = ImageView(this)
//        Picasso.get().load(auth.currentUser?.photoUrl.toString()).into(icon)
//        menu?.findItem(R.id.avatar)?.actionView = icon
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.avatar){
            Log.d(TAG, "Change fragment")
            val navController = findNavController(R.id.nav_host_fragment)
            navController.navigate(R.id.navigation_account)
        }
        return super.onOptionsItemSelected(item)
    }

    fun speedDialView(){
        floatingActionButton.inflate(R.menu.floating_action_button_menu)
        floatingActionButton.setOnActionSelectedListener(SpeedDialView.OnActionSelectedListener { actionItem -> when (actionItem.id){
            R.id.action_show_home -> {
                Toast.makeText(applicationContext,R.string.title_home, Toast.LENGTH_SHORT).show()
                floatingActionButton.close()
                return@OnActionSelectedListener true
            }
            R.id.action_show_notifications -> {
                Toast.makeText(applicationContext,R.string.title_notifications, Toast.LENGTH_SHORT).show()
                floatingActionButton.close()
                return@OnActionSelectedListener true
            }
            R.id.action_show_dashboard -> {
                Toast.makeText(applicationContext,R.string.title_dashboard, Toast.LENGTH_SHORT).show()
                floatingActionButton.close()
                return@OnActionSelectedListener true
            }
        }
            false
        })
    }

    fun initialize(){
        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = Firebase.auth //ktx
        // [END initialize_auth]
        floatingActionButton = findViewById(R.id.floatingActionButton)
        speedDialView()
    }

    fun signOut(){
        auth.signOut()
        // [START config_signOut]
        // Configure Google Sign Out
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this,gso)
        googleSignInClient.signOut().addOnCompleteListener(this, OnCompleteListener {
            finish()
            startActivity(Intent(this@MainActivity,GoogleSignInActivity::class.java))
            Log.d(TAG, "logout:success")
        })
        // [END config_signOut]
    }
}
