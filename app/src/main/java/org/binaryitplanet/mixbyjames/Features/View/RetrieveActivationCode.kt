package org.binaryitplanet.mixbyjames.Features.View

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import org.binaryitplanet.mixbyjames.R
import org.binaryitplanet.mixbyjames.databinding.ActivityRetrieveActivationCodeBinding

class RetrieveActivationCode : AppCompatActivity() {

    private val TAG = "RetrieveActivation"
    private lateinit var binding: ActivityRetrieveActivationCodeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrieve_activation_code)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_retrieve_activation_code)

        binding.goBack.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.righttoposition, R.anim.positiontoright)
    }
}