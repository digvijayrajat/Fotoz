package com.digvijay.fotoz.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.digvijay.fotoz.R

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Handler().postDelayed({
            val mainIntent = Intent(this, HomeActivity::class.java)
            startActivity(mainIntent)
            finish()
        }, 3000)
    }
}
