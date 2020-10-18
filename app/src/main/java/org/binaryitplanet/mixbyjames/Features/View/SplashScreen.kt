package org.binaryitplanet.mixbyjames.Features.View

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import org.binaryitplanet.mixbyjames.R
import org.binaryitplanet.mixbyjames.Utils.Config

@Suppress("DEPRECATION")
class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        window.decorView.apply {
            systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        }



        Handler(Looper.getMainLooper()).postDelayed({

            var intent = if (isActivated()) {
                Intent(applicationContext, MainActivity::class.java)
            } else {
                Intent(applicationContext, ActivationScreen::class.java)
            }
            startActivity(intent)
            overridePendingTransition(R.anim.lefttoright, R.anim.righttoleft)
            finish()
        }, 3000)
    }

    private fun isActivated(): Boolean {

        val sharedPreferences = getSharedPreferences(
            Config.SHARED_PREFERENCE,
            Context.MODE_PRIVATE
        )!!

        return sharedPreferences.getBoolean(Config.IS_ACTIVATED, false)
    }
}