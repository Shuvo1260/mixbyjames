package org.binaryitplanet.mixbyjames.Features.View

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.ToneGenerator.MAX_VOLUME
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import org.binaryitplanet.mixbyjames.Features.Model.UserInfoModel
import org.binaryitplanet.mixbyjames.Features.Model.UserInfoModelIml
import org.binaryitplanet.mixbyjames.Features.Presenter.AudioPresenter
import org.binaryitplanet.mixbyjames.Features.Presenter.AudioPresenterIml
import org.binaryitplanet.mixbyjames.R
import org.binaryitplanet.mixbyjames.Utils.Config
import org.binaryitplanet.mixbyjames.databinding.ActivityMainBinding
import kotlin.math.ln


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), MainActivityView {

    private val TAG = "MainActivity"
    private lateinit var binding: ActivityMainBinding

    private var audioUriString: String? = null
    private var filename: String? = null

    private lateinit var model: UserInfoModel
    private lateinit var presenter: AudioPresenter
    private lateinit var sharedPreferences: SharedPreferences

    private var mediaPlayer: MediaPlayer? = null

    private var volumeProgress: Int = 0

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        model = UserInfoModelIml(this)
        presenter = AudioPresenterIml(this, this, model)

        sharedPreferences = getSharedPreferences(
                Config.SHARED_PREFERENCE,
                Context.MODE_PRIVATE
        )!!

        audioUriString = sharedPreferences.getString(Config.AUDIO_URI, null)
        filename = sharedPreferences.getString(Config.FILE_NAME, null)


        binding.playButton.setOnClickListener {

            binding.topMessage.text = Config.NOW_PLAYING
            if (binding.pauseButton.visibility == View.GONE)
                binding.pauseButton.visibility = View.VISIBLE

            if (binding.playButton.visibility == View.VISIBLE)
                binding.playButton.visibility = View.INVISIBLE

            playAudio(audioUriString)
        }

        binding.pauseButton.setOnClickListener {

            binding.topMessage.text = Config.NOW_IN_PAUSE
            if (binding.playButton.visibility == View.INVISIBLE)
                binding.playButton.visibility = View.VISIBLE

            if (binding.pauseButton.visibility == View.VISIBLE)
                binding.pauseButton.visibility = View.GONE

            if (mediaPlayer != null) {
                mediaPlayer?.stop()
                mediaPlayer?.release()
                mediaPlayer = null
            }
        }

        requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                Config.STORAGE_REQUEST_CODE
        )

        volumeProgress = sharedPreferences.getInt(Config.VOLUME, 100)
        binding.volume.progress = volumeProgress

        binding.volume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.d(TAG, "Progress: $progress")

                val volume = getVolume(progress)

                if (mediaPlayer != null)
                    mediaPlayer?.setVolume(volume, volume)

                volumeProgress = progress

                var editor = sharedPreferences.edit()
                editor.putInt(Config.VOLUME, volumeProgress)
                editor.apply()
                editor.commit()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                //
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                //
            }

        })

    }

    private fun getVolume(progress: Int): Float {
        return (1 - ln((MAX_VOLUME - progress).toDouble()) / ln(MAX_VOLUME.toDouble())).toFloat()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Config.STORAGE_REQUEST_CODE
                && grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (audioUriString.isNullOrEmpty()) {
                binding.topMessage.text = Config.FILE_DOWNLOADING_MESSAGE
                if (binding.loading.visibility == View.GONE)
                    binding.loading.visibility = View.VISIBLE

                if (binding.audioLayout.visibility == View.VISIBLE)
                    binding.audioLayout.visibility = View.GONE
                presenter.getAudio(filename!!)
            } else {

                setupAudioLayout()

            }
        }
    }

    private fun setupAudioLayout() {

        binding.topMessage.text = Config.FILE_LOADED_SUCCESSFULLY

        if (binding.loading.visibility == View.VISIBLE)
            binding.loading.visibility = View.GONE

        if (binding.audioLayout.visibility == View.GONE)
            binding.audioLayout.visibility = View.VISIBLE

        audioUriString = sharedPreferences.getString(Config.AUDIO_URI, null)
        filename = sharedPreferences.getString(Config.FILE_NAME, null)
    }

    private fun playAudio(audioUriString: String?) {
        var audioUri = Uri.parse(Uri.decode(audioUriString))
        mediaPlayer = MediaPlayer.create(this, audioUri)

        var volume = getVolume(volumeProgress)

        mediaPlayer?.setVolume(volume, volume)
        mediaPlayer?.start()

        mediaPlayer?.setOnCompletionListener {
            mediaPlayer?.start()
        }
    }

    override fun onAudioDownloadingListener(status: Boolean, message: String) {
        Log.d(TAG, "Downloaded Status: $status $message")

        if (status) {
            setupAudioLayout()
        }
        if (binding.loading.visibility == View.VISIBLE)
            binding.loading.visibility = View.GONE
        Toast.makeText(
                this,
                message,
                Toast.LENGTH_SHORT
        ).show()
    }
}