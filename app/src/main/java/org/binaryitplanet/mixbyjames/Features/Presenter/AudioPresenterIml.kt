package org.binaryitplanet.mixbyjames.Features.Presenter

import android.util.Log
import okhttp3.ResponseBody
import org.binaryitplanet.mixbyjames.Common.RequestCompleteListener
import org.binaryitplanet.mixbyjames.Features.Model.UserInfoModel
import org.binaryitplanet.mixbyjames.Features.View.MainActivityView
import org.binaryitplanet.mixbyjames.Utils.Config
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.io.OutputStream

class AudioPresenterIml(
    private val view: MainActivityView,
    private val model: UserInfoModel
): AudioPresenter {

    private val TAG = "AudioPresenter"

    override fun getAudio(filename: String) {
        model.getAudio(
            filename,
            object : RequestCompleteListener<ResponseBody> {
                override fun onRequestSuccess(data: ResponseBody) {
                    if (writeResponseBodyToStorage(data, filename)) {
                        view.onAudioDownloadingListener(true, "Downloading success")
                    } else {
                        view.onAudioDownloadingListener(false, "Downloading failed")
                    }
                }

                override fun onRequestFailed(message: String) {
                    view.onAudioDownloadingListener(false, message)
                }

            }
        )
    }


    private fun writeResponseBodyToStorage(body: ResponseBody, fileName: String): Boolean {
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
            return true
        } catch (e: Exception) {
            Log.d(TAG, "WriteException: ${e.message}")
            return false
        }
    }
}