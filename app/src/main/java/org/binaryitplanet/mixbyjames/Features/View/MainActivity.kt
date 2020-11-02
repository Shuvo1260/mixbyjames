package org.binaryitplanet.mixbyjames.Features.View

import android.Manifest
import android.content.Context
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.AssetFileDescriptor
import android.media.MediaPlayer
import android.media.ToneGenerator.MAX_VOLUME
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
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
import java.lang.Exception
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
    private var secondMediaPlayer: MediaPlayer? = null

    private var pinkFirstMediaPlayer: MediaPlayer? = null
    private var pinkSecondMediaPlayer: MediaPlayer? = null

    private var volumeProgress: Int = 0
    private var pinkVolumeProgress: Int = 0

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
            pauseMedia()
        }

        requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE),
                Config.STORAGE_REQUEST_CODE
        )

        volumeProgress = sharedPreferences.getInt(Config.VOLUME, 100)
        pinkVolumeProgress = sharedPreferences.getInt(Config.PINK_VOLUME, 100)

        binding.volume.progress = volumeProgress
        binding.pinkVolume.progress = pinkVolumeProgress

        binding.volume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.d(TAG, "Progress: $progress")

                val volume = getVolume(progress)

                if (mediaPlayer != null)
                    mediaPlayer?.setVolume(volume, volume)

                if (secondMediaPlayer != null)
                    secondMediaPlayer?.setVolume(volume, volume)

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


        binding.pinkVolume.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {

                Log.d(TAG, "PinkProgress: $progress")

                val volume = getVolume(progress)

                if (pinkFirstMediaPlayer != null)
                    pinkFirstMediaPlayer?.setVolume(volume, volume)

                if (pinkSecondMediaPlayer != null)
                    pinkSecondMediaPlayer?.setVolume(volume, volume)

                pinkVolumeProgress = progress

                var editor = sharedPreferences.edit()
                editor.putInt(Config.PINK_VOLUME, pinkVolumeProgress)
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

    private fun pauseMedia() {

        binding.topMessage.text = Config.NOW_IN_PAUSE
        if (binding.playButton.visibility == View.INVISIBLE)
            binding.playButton.visibility = View.VISIBLE

        if (binding.pauseButton.visibility == View.VISIBLE)
            binding.pauseButton.visibility = View.GONE

        if (mediaPlayer != null) {
            if (mediaPlayer!!.isPlaying)
                mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }

        if (secondMediaPlayer != null) {
            if (secondMediaPlayer!!.isPlaying)
                secondMediaPlayer?.stop()
            secondMediaPlayer?.release()
            secondMediaPlayer = null
        }

        if (pinkFirstMediaPlayer != null) {
            if (pinkFirstMediaPlayer!!.isPlaying)
                pinkFirstMediaPlayer?.stop()
            pinkFirstMediaPlayer?.release()
            pinkFirstMediaPlayer = null
        }

        if (pinkSecondMediaPlayer != null) {
            if (pinkSecondMediaPlayer!!.isPlaying)
                pinkSecondMediaPlayer?.stop()
            pinkSecondMediaPlayer?.release()
            pinkSecondMediaPlayer = null
        }
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
        try {
            var audioUri = Uri.parse(Uri.decode(audioUriString))
            mediaPlayer = MediaPlayer.create(this, audioUri)
            secondMediaPlayer = MediaPlayer.create(this, audioUri)

            var volume = getVolume(volumeProgress)

            mediaPlayer?.setVolume(volume, volume)
            secondMediaPlayer?.setVolume(volume, volume)
            mediaPlayer?.start()
            mediaPlayer?.setNextMediaPlayer(secondMediaPlayer)

            mediaPlayer?.setOnCompletionListener {
                try {
                    if (it != null) {
                        if (it.isPlaying)
                            it.stop()
                        it.reset()
                    }
                    it.setDataSource(this, audioUri)
                    it.prepare()
                    it.setOnPreparedListener {
                        volume = getVolume(volumeProgress)
                        Log.d(TAG, "FirstMediaPlayer $volume")
                        secondMediaPlayer?.setVolume(volume, volume)
                        mediaPlayer?.setVolume(volume, volume)
                        secondMediaPlayer?.setNextMediaPlayer(mediaPlayer!!)
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "prepare: ${e.message}")
                    pauseMedia()
                }

            }

            secondMediaPlayer?.setOnCompletionListener {
                try {
                    if (it != null) {
                        if (it.isPlaying)
                            it.stop()
                        it.reset()
                    }
                    it.setDataSource(this, audioUri)
                    it.prepare()
                    it.setOnPreparedListener {
                        volume = getVolume(volumeProgress)
                        Log.d(TAG, "SecondMediaPlayer $volume")
                        mediaPlayer?.setVolume(volume, volume)
                        secondMediaPlayer?.setVolume(volume, volume)
                        mediaPlayer?.setNextMediaPlayer(secondMediaPlayer!!)
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "prepare2: ${e.message}")
                    pauseMedia()
                }
            }


            // Pink media player
            val assetFile: AssetFileDescriptor = resources.openRawResourceFd(R.raw.pink_noise)
            pinkFirstMediaPlayer = MediaPlayer.create(this, R.raw.pink_noise)
            pinkSecondMediaPlayer = MediaPlayer.create(this, R.raw.pink_noise)

            var pinkVolume = getVolume(pinkVolumeProgress)

            pinkFirstMediaPlayer?.setVolume(pinkVolume, pinkVolume)
            pinkSecondMediaPlayer?.setVolume(pinkVolume, pinkVolume)
            pinkFirstMediaPlayer?.start()
            pinkFirstMediaPlayer?.setNextMediaPlayer(pinkSecondMediaPlayer)

            pinkFirstMediaPlayer?.setOnCompletionListener {
                try {
                    if (it != null) {
                        if (it.isPlaying)
                            it.stop()
                        it.reset()
                    }
                    if (assetFile != null) {
                        it.setDataSource(
                                assetFile.fileDescriptor,
                                assetFile.startOffset,
                                assetFile.length
                        )
                        it.prepare()
                        it.setOnPreparedListener {
                            pinkVolume = getVolume(pinkVolumeProgress)
                            Log.d(TAG, "PinkFirstMediaPlayer $pinkVolume")
                            pinkSecondMediaPlayer?.setVolume(pinkVolume, pinkVolume)
                            pinkFirstMediaPlayer?.setVolume(pinkVolume, pinkVolume)
                            pinkSecondMediaPlayer?.setNextMediaPlayer(pinkFirstMediaPlayer!!)
                        }
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "PinkPrepare: ${e.message}")
                    pauseMedia()
                }
            }

            pinkSecondMediaPlayer?.setOnCompletionListener {
                try {
                    if (it != null) {
                        if (it.isPlaying)
                            it.stop()
                        it.reset()
                    }
                    if (assetFile != null) {
                        it.setDataSource(
                                assetFile.fileDescriptor,
                                assetFile.startOffset,
                                assetFile.length
                        )
                        it.prepare()

                        it.setOnPreparedListener {
                            pinkVolume = getVolume(pinkVolumeProgress)
                            Log.d(TAG, "PinkSecondMediaPlayer $pinkVolume")
                            pinkFirstMediaPlayer?.setVolume(pinkVolume, pinkVolume)
                            pinkSecondMediaPlayer?.setVolume(pinkVolume, pinkVolume)
                            pinkFirstMediaPlayer?.setNextMediaPlayer(pinkSecondMediaPlayer!!)
                        }
                    }
                } catch (e: Exception) {
                    Log.d(TAG, "PinkPrepare2: ${e.message}")
                    pauseMedia()
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "MediaPlayerIssue: ${e.message}")
            playAudio(audioUriString)
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