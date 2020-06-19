package com.example.templatebottomnavmenu

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar

class SignIn : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var login: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        initialize()
        signInGoogle()
        actionListener()
    }

    fun signInGoogle(){
        progressBar.visibility = View.VISIBLE
    }

    fun actionListener(){
        login.setOnClickListener{
            val intentGoToMainActivity = Intent(this, MainActivity::class.java)
            startActivity(intentGoToMainActivity)
        }
    }

    fun initialize(){
        progressBar = findViewById(R.id.progress_circular)
        login = findViewById(R.id.login)
    }

}