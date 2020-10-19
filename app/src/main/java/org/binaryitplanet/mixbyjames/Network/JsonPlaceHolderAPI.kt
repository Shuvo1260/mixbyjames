package org.binaryitplanet.mixbyjames.Network

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface JsonPlaceHolderAPI {

    @GET("user.php")
    fun getUserData(
        @Query("email") email: String,
        @Query("activationcode") activationCode: String
    ): Call<ResponseBody>


    @GET("send_activation_code.php")
    fun getActivationCodeInfo(
        @Query("email") email: String
    ): Call<ResponseBody>


    @GET("files/{file}")
    fun getAudio(
        @Path("file") file: String
    ): Call<ResponseBody>
}