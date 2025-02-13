package com.example.playlistmaker.player.ui.viewmodel

import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.player.domain.PlayerState
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale
import java.util.concurrent.Executors

class PlayerViewModel : ViewModel() {
    private var mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())
    private val executor = Executors.newCachedThreadPool()

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.StateDefault)
    private val currentTrackTimeLiveData = MutableLiveData<String>()

    override fun onCleared() {
        playerStateLiveData.postValue(PlayerState.StateDefault)
        mediaPlayer.release()
        super.onCleared()
    }

    fun getPlayerStateLiveData(): LiveData<PlayerState> = playerStateLiveData

    fun getCurrentTrackTimeLiveData(): LiveData<String> = currentTrackTimeLiveData

    fun preparePlayer(url: String) {
        executor.execute {
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
        }

        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.postValue(PlayerState.StatePrepared)
        }

        mediaPlayer.setOnCompletionListener {
            playerStateLiveData.postValue(PlayerState.StatePrepared)
        }
    }

    fun playbackControl() {
        val playerState = playerStateLiveData.value

        when(playerState) {
            PlayerState.StatePlaying -> pausePlayer()
            PlayerState.StatePrepared, PlayerState.StatePaused -> startPlayer()
            else -> Log.i("INFO", "Media player is in default state")
        }
    }

    fun startPlayer() {
        playerStateLiveData.postValue(PlayerState.StatePlaying)
        mediaPlayer.start()

        updatePlaybackTime()
    }

    fun pausePlayer() {
        playerStateLiveData.postValue(PlayerState.StatePaused)
        mediaPlayer.pause()
    }

    private fun updatePlaybackTime() {
        handler.post(object : Runnable {
            override fun run() {
                val playerState = playerStateLiveData.value
                when (playerState) {
                    PlayerState.StatePlaying -> {
                        val s = dateFormat.format(mediaPlayer.currentPosition)
                        currentTrackTimeLiveData.postValue(dateFormat.format(mediaPlayer.currentPosition))
                        Log.i("TIME", s)
                        handler.postDelayed(this, UPDATE_TIME_INTERVAL)
                    }

                    else -> handler.removeCallbacks(this)
                }
            }
        })
    }


    fun extractYear(releaseDate: String): String {
        var year = ""

        try {
            val zonedDateTime = ZonedDateTime.parse(releaseDate, DateTimeFormatter.ISO_ZONED_DATE_TIME)
            year = zonedDateTime.year.toString()
        } catch (e: DateTimeParseException) {
            Log.i("Info","Не удалось преобразовать строку releaseDate в ZonedDateTime")
        }

        return year
    }

    companion object {
        private const val UPDATE_TIME_INTERVAL = 200L
    }
}