package org.binaryitplanet.mixbyjames.Features.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import org.binaryitplanet.mixbyjames.R
import org.binaryitplanet.mixbyjames.databinding.ActivityActivationScreenBinding

class ActivationScreen : AppCompatActivity() {

    private val TAG = "ActivationScreen"
    private lateinit var binding: ActivityActivationScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activation_screen)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_activation_screen)

        binding.activateButton.setOnClickListener {
            Toast.makeText(
                    this,
                    "Activate",
                    Toast.LENGTH_SHORT
            ).show()
        }

        binding.noActivationCode.setOnClickListener {
            val intent = Intent(this, RetrieveActivationCode::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.righttoposition, R.anim.positiontoright)
        }
    }
}