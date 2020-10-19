package org.binaryitplanet.mixbyjames.Features.Model

import android.content.Context
import android.util.Log
import okhttp3.ResponseBody
import org.binaryitplanet.mixbyjames.Common.RequestCompleteListener
import org.binaryitplanet.mixbyjames.Network.JsonPlaceHolderAPI
import org.binaryitplanet.mixbyjames.Network.RetrofitClient
import org.binaryitplanet.mixbyjames.Utils.Config
import org.binaryitplanet.mixbyjames.Utils.UserInfoUtils
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class UserInfoModelIml(
    private val context: Context
): UserInfoModel {

    private val TAG = "UserInfoModel"

    private var retrofit: Retrofit = RetrofitClient.getInstance()
    private var apiHolder: JsonPlaceHolderAPI

    init {
        apiHolder = retrofit.create(JsonPlaceHolderAPI::class.java)
    }


    override fun getUserInfo(
        activationCode: String,
        email: String,
        callback: RequestCompleteListener<UserInfoUtils>
    ) {

        var response: Call<ResponseBody> = apiHolder.getUserData(
            email,
            activationCode
        )


        response.enqueue(object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d(TAG, "Response: ${response.isSuccessful} ${response.body()}")
                if (response.isSuccessful) {
                    val body = response.body()?.string()
                    Log.d(TAG, "Body: ${body?.get(0).toString()}")
                    try {
                        val responseJson = JSONObject(body)
                        Log.d(TAG, "Json: ${responseJson[Config.RESPONSE]}")
                        val responseBody = responseJson[Config.RESPONSE].toString()
                        val jsonObject = JSONObject(responseBody)

                        Log.d(TAG, "JsonObject: ${jsonObject[Config.FILE_NAME]}")


                        if (jsonObject[Config.STATUS].toString() == Config.STATUS_SUCCESS) {
                            val userInfo = UserInfoUtils(
                                jsonObject[Config.STATUS].toString(),
                                jsonObject[Config.USER_ID].toString(),
                                jsonObject[Config.FIRST_NAME].toString(),
                                jsonObject[Config.LAST_NAME].toString(),
                                jsonObject[Config.FILE_NAME].toString(),
                                jsonObject[Config.MESSAGE].toString()
                            )

                            callback.onRequestSuccess(userInfo)
                        } else {
                            callback.onRequestFailed(jsonObject[Config.MESSAGE].toString())
                        }

                    } catch (e: Exception) {
                        Log.d(TAG, "JsonException: ${e.message}")
                        callback.onRequestFailed(e.message!!)
                    }
                } else {
                    callback.onRequestFailed("Userinfo fetching Failed")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d(TAG, "ResponseException: ${t.message}")

                callback.onRequestFailed(t.message!!)
            }

        })
    }

    override fun getAudio(filename: String, callback: RequestCompleteListener<ResponseBody>) {
        var response = apiHolder.getAudio(filename)

        response.enqueue(object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    callback.onRequestSuccess(response.body()!!)
                } else {
                    callback.onRequestFailed("Audio downloading failed")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d(TAG, "AudioResponseException: ${t.message}")

                callback.onRequestFailed(t.message!!)
            }
        })
    }

    override fun getActivationCode(
        email: String,
        callback: RequestCompleteListener<UserInfoUtils>
    ) {
        var response: Call<ResponseBody> = apiHolder.getActivationCodeInfo(
            email
        )


        response.enqueue(object : Callback<ResponseBody>{
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {

                Log.d(TAG, "Response: ${response.isSuccessful} ${response.body()}")

                if (response.isSuccessful) {
                    val body = response.body()?.string()
                    Log.d(TAG, "Body: ${body?.get(0).toString()}")
                    try {
                        val responseJson = JSONObject(body)
                        Log.d(TAG, "Json: ${responseJson[Config.RESPONSE]}")
                        val responseBody = responseJson[Config.RESPONSE].toString()
                        val jsonObject = JSONObject(responseBody)

                        Log.d(TAG, "JsonObject: ${jsonObject[Config.FILE_NAME]}")

                        if (jsonObject[Config.STATUS].toString() == Config.STATUS_SUCCESS) {
                            val userInfo = UserInfoUtils(
                                jsonObject[Config.STATUS].toString(),
                                jsonObject[Config.USER_ID].toString(),
                                jsonObject[Config.FIRST_NAME].toString(),
                                jsonObject[Config.LAST_NAME].toString(),
                                jsonObject[Config.FILE_NAME].toString(),
                                jsonObject[Config.MESSAGE].toString()
                            )

                            callback.onRequestSuccess(userInfo)
                        } else {
                            callback.onRequestFailed(jsonObject[Config.MESSAGE].toString())
                        }

                    } catch (e: Exception) {
                        Log.d(TAG, "JsonException: ${e.message}")
                        callback.onRequestFailed(e.message!!)
                    }
                } else {
                    callback.onRequestFailed("Activation Code sending Failed")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.d(TAG, "ResponseException: ${t.message}")

                callback.onRequestFailed(t.message!!)
            }

        })
    }
}