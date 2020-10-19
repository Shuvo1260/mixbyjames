package org.binaryitplanet.mixbyjames.Features.View

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import org.binaryitplanet.mixbyjames.Features.Model.UserInfoModel
import org.binaryitplanet.mixbyjames.Features.Model.UserInfoModelIml
import org.binaryitplanet.mixbyjames.Features.Presenter.UserInfoPresenter
import org.binaryitplanet.mixbyjames.Features.Presenter.UserInfoPresenterIml
import org.binaryitplanet.mixbyjames.R
import org.binaryitplanet.mixbyjames.Utils.Config
import org.binaryitplanet.mixbyjames.Utils.UserInfoUtils
import org.binaryitplanet.mixbyjames.databinding.ActivityActivationScreenBinding

class ActivationScreen : AppCompatActivity(), ActivationScreenView {

    private val TAG = "ActivationScreen"
    private lateinit var binding: ActivityActivationScreenBinding

    private lateinit var model: UserInfoModel
    private lateinit var presenter: UserInfoPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_activation_screen)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_activation_screen)

        model = UserInfoModelIml(this)
        presenter = UserInfoPresenterIml(this, model)

        binding.activateButton.setOnClickListener {
            Toast.makeText(
                this,
                Config.ACTIVATING_MESSAGE,
                Toast.LENGTH_SHORT
            ).show()
            activate()
        }

        binding.noActivationCode.setOnClickListener {
            val intent = Intent(this, RetrieveActivationCode::class.java)
            startActivity(intent)
            overridePendingTransition(R.anim.righttoposition, R.anim.positiontoright)
        }
    }

    private fun activate() {

        val activationCode = binding.activationCode.text.toString()
        val email = binding.email.text.toString()


        if (checkValidation(email, activationCode))
            presenter.getUserInfo(activationCode, email)
    }

    private fun checkValidation(email: String, activationCode: String): Boolean {
        if (activationCode.isNullOrEmpty()) {
            binding.activationCode.error = Config.REQUIRED_FIELD
            return false
        }

        if (email.isNullOrEmpty()) {
            binding.email.error = Config.REQUIRED_FIELD
            return false
        }

        return true
    }

    override fun onActivationSuccessListener(userInfoUtils: UserInfoUtils) {

        binding.activationErrorMessage.visibility = View.INVISIBLE
        Log.d(TAG, "UserInfo: $userInfoUtils")
    }

    override fun onActivationFailedListener(message: String) {
        binding.activationErrorMessage.visibility = View.VISIBLE

        Log.d(TAG, "ActivationFailed: $message")
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.lefttoright, R.anim.righttoleft)
    }
}