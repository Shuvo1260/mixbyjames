package org.binaryitplanet.mixbyjames.Features.Presenter

import android.util.Log
import org.binaryitplanet.mixbyjames.Common.RequestCompleteListener
import org.binaryitplanet.mixbyjames.Features.Model.UserInfoModel
import org.binaryitplanet.mixbyjames.Features.View.ActivationScreenView
import org.binaryitplanet.mixbyjames.Utils.Config
import org.binaryitplanet.mixbyjames.Utils.UserInfoUtils

@Suppress("DEPRECATION")
class UserInfoPresenterIml(
    private val view: ActivationScreenView,
    private val model: UserInfoModel
): UserInfoPresenter {

    private val TAG = "UserInfoPresenter"

    override fun getUserInfo(activationCode: String, email: String) {
        model.getUserInfo(
            activationCode,
            email,
            object : RequestCompleteListener<UserInfoUtils>{
                override fun onRequestSuccess(data: UserInfoUtils) {
                    Log.d(TAG, "Data: $data")
                    if (data.status == Config.STATUS_SUCCESS)
                        view.onActivationSuccessListener(data)
                    else
                        view.onActivationFailedListener(data.message)
                }

                override fun onRequestFailed(message: String) {
                    view.onActivationFailedListener(message)
                }

            }
        )
    }

    override fun getActivationCode(email: String) {
        model.getActivationCode(
            email,
            object : RequestCompleteListener<UserInfoUtils>{
                override fun onRequestSuccess(data: UserInfoUtils) {
                    Log.d(TAG, "Data: $data")
                    if (data.status == Config.STATUS_SUCCESS)
                        view.onActivationSuccessListener(data)
                    else
                        view.onActivationFailedListener(data.message)
                }

                override fun onRequestFailed(message: String) {
                    view.onActivationFailedListener(message)
                }

            }
        )
    }

}