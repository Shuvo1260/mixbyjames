package org.binaryitplanet.mixbyjames.Features.Presenter

import android.content.Context
import android.os.Environment
import android.util.Log
import okhttp3.ResponseBody
import org.binaryitplanet.mixbyjames.Common.RequestCompleteListener
import org.binaryitplanet.mixbyjames.Features.Model.UserInfoModel
import org.binaryitplanet.mixbyjames.Features.View.ActivationScreenView
import org.binaryitplanet.mixbyjames.Utils.Config
import org.binaryitplanet.mixbyjames.Utils.UserInfoUtils
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

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

    override fun getAudio(filename: String) {
        model.getAudio(
            filename,
            object : RequestCompleteListener<ResponseBody>{
                override fun onRequestSuccess(data: ResponseBody) {
                    writeResponseBodyToStorage(data, filename)
                }

                override fun onRequestFailed(message: String) {
                    //
                }

            }
        )
    }

    override fun getActivationCode(email: String) {
        model.getActivationCode(
            email,
            object : RequestCompleteListener<UserInfoUtils>{
                override fun onRequestSuccess(data: UserInfoUtils) {
                    //
                }

                override fun onRequestFailed(message: String) {
                    //
                }

            }
        )
    }


    private fun writeResponseBodyToStorage(body: ResponseBody, fileName: String) {
        try {
            val folder = File(Config.SD_CARD_PATH)

            if (!folder.exists()) {
                folder.mkdirs()
            }

            val file = File(
                folder,
                fileName
            )

            var inputStream: InputStream? = null
            var outputStream: OutputStream? = null
            var fileSize = body.contentLength()
            var fileReader = ByteArray(fileSize.toInt())
            var fileSizeDownload = 0
            inputStream = body.byteStream()

            outputStream = FileOutputStream(file)
            while (true) {
                var read = inputStream.read(fileReader)
                Log.d(TAG, "FileReader: ${inputStream}")
                if (read == -1)
                    break
                outputStream.write(fileReader, 0, read)
                fileSizeDownload += read

                Log.d(TAG, "File is donwloaded: $fileSizeDownload of $fileSize")
            }
            Log.d(TAG, "Downloaded")
            outputStream.flush()
            inputStream?.close()

            outputStream?.close()
        } catch (e: Exception) {
            Log.d(TAG, "WriteException: ${e.message}")
        }
    }
}