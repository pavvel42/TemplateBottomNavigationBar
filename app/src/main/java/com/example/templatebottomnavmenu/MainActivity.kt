package com.example.templatebottomnavmenu

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initialize()
        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications))
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        menu?.findItem(R.id.avatar)?.setIcon(R.drawable.ic_google_logo) //avatar
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.avatar){
            Log.d(TAG, "Change fragment")
            //supportFragmentManager.beginTransaction().replace(R.id.navigation_dashboard,DashboardFragment()).commit()
        }
        return super.onOptionsItemSelected(item)
    }

    fun initialize(){
        // [START initialize_auth]
        // Initialize Firebase Auth
        auth = Firebase.auth //ktx
        // [END initialize_auth]
    }

    private fun signOut(){
        auth.signOut()
        Log.d(TAG, "logout:success")
        finish()
        startActivity(Intent(this@MainActivity,GoogleSignInActivity::class.java))
    }

    override fun onBackPressed() {
        super.onBackPressed()
        signOut()
    }
}
