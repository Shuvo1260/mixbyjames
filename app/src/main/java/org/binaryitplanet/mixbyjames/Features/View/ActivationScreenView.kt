package org.binaryitplanet.mixbyjames.Features.View

import org.binaryitplanet.mixbyjames.Utils.UserInfoUtils

interface ActivationScreenView {
    fun onActivationSuccessListener(userInfoUtils: UserInfoUtils)
    fun onActivationFailedListener(message: String)
}