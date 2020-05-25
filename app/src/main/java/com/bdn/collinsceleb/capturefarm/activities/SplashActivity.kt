package com.bdn.collinsceleb.capturefarm.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.bdn.collinsceleb.capturefarm.R

class SplashActivity : AppCompatActivity() {
    private val SPLASH_TIMEOUT: Long = 5000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
    }

    //    @Override
    //    protected void onStart() {
    //        super.onStart();
    //    }
    override fun onStop() {
        super.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        finish()
    }

    override fun onPause() {
        super.onPause()
        finish()
    }

    override fun onResume() {
        super.onResume()
        startNextActivity()
    }

    private fun startNextActivity() {
        val intent = Intent(this, SignInActivity::class.java)
        Handler().postDelayed({ startActivity(intent) }, SPLASH_TIMEOUT)
    }
}