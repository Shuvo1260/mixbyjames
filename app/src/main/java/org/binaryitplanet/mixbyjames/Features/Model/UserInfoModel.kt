package org.binaryitplanet.mixbyjames.Features.Model

import okhttp3.ResponseBody
import org.binaryitplanet.mixbyjames.Common.RequestCompleteListener
import org.binaryitplanet.mixbyjames.Utils.UserInfoUtils

interface UserInfoModel {
    fun getUserInfo(
        activationCode: String,
        email: String,
        callback: RequestCompleteListener<UserInfoUtils>
    )

    fun getAudio(
        filename: String,
        callback: RequestCompleteListener<ResponseBody>
    )


    fun getActivationCode(
        email: String,
        callback: RequestCompleteListener<UserInfoUtils>
    )
}