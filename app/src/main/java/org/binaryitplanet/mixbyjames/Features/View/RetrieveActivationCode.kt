package org.binaryitplanet.mixbyjames.Features.View

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
import org.binaryitplanet.mixbyjames.databinding.ActivityRetrieveActivationCodeBinding

class RetrieveActivationCode : AppCompatActivity(), ActivationScreenView {

    private val TAG = "RetrieveActivation"
    private lateinit var binding: ActivityRetrieveActivationCodeBinding

    private lateinit var model: UserInfoModel
    private lateinit var presenter: UserInfoPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_retrieve_activation_code)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_retrieve_activation_code)

        model = UserInfoModelIml(this)
        presenter = UserInfoPresenterIml(this, this, model)

        binding.goBack.setOnClickListener {
            onBackPressed()
        }

        binding.retrieveActivationCodeButton.setOnClickListener {
            retrieveCode()
        }
    }

    private fun retrieveCode() {
        val email = binding.email.text.toString()

        if (checkValidity(email)) {
            Toast.makeText(
                this,
                Config.REQUESTING_MESSAGE,
                Toast.LENGTH_SHORT
            ).show()
            presenter.getActivationCode(email)
        }
    }

    private fun checkValidity(email: String): Boolean {

        if (email.isNullOrEmpty()) {
            binding.email.error = Config.REQUIRED_FIELD
            return false
        }
        return true
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.righttoposition, R.anim.positiontoright)
    }

    override fun onActivationSuccessListener(userInfoUtils: UserInfoUtils) {
        Log.d(TAG, "UserInfoUtils: $userInfoUtils")
        if (binding.retrieveCodeMessage.visibility == View.INVISIBLE)
            binding.retrieveCodeMessage.visibility = View.VISIBLE
    }

    override fun onActivationFailedListener(message: String) {
        if (binding.retrieveCodeMessage.visibility == View.VISIBLE)
            binding.retrieveCodeMessage.visibility = View.INVISIBLE
        Toast.makeText(
            this,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }
}