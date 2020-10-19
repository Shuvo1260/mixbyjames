package org.binaryitplanet.mixbyjames.Features.Presenter

interface UserInfoPresenter {
    fun getUserInfo(
        activationCode: String,
        email: String
    )

    fun getAudio(
        filename: String
    )

    fun getActivationCode(
        email: String
    )
}