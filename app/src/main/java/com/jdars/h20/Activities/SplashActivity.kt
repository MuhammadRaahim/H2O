package com.jdars.h20.Activities

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.horizam.skbhub.Utils.Constants
import com.jdars.h20.MainActivity
import com.jdars.h20.R
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        setSplash()
    }

    private fun setSplash() {
        Handler(Looper.getMainLooper()).postDelayed(Runnable {
            checkLoginInfo()
        }, Constants.SPLASH_DISPLAY_LENGTH.toLong())
    }

    private fun checkLoginInfo(){
        val user = Firebase.auth.currentUser
        val intent = if (user != null){
            Intent(this@SplashActivity, MainActivity::class.java)
        }else{
            Intent(this@SplashActivity, LoginActivity::class.java)
        }
        startActivity(intent)
        finish()
    }
}