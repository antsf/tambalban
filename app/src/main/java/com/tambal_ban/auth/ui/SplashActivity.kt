package com.tambal_ban.auth.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import com.tambal_ban.R
import com.tambal_ban.TambalBanApp
import com.tambal_ban.map.ui.MainActivity

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val authPrefs = (application as TambalBanApp).authPrefs

        Handler(Looper.getMainLooper()).postDelayed({
            // val target = if (authPrefs.isLoggedIn()) {
            val target = Intent(this, MainActivity::class.java)
            // } else {
            //     Intent(this, LoginActivity::class.java)
            // }
            target.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(target)
            finish()
        }, 1200)
    }
}
