package com.example.playlistmaker.player.ui.viewmodel

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.core.model.Track
import com.example.playlistmaker.db.domain.interactor.FavoriteTracksInteractor
import com.example.playlistmaker.player.domain.PlayerState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor
) : ViewModel() {
    private var mediaPlayer = MediaPlayer()
    private var timerJob: Job? = null

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.StateDefault)
    fun getPlayerStateLiveData(): LiveData<PlayerState> = playerStateLiveData

    private val currentTrackTimeLiveData = MutableLiveData<String>()
    fun getCurrentTrackTimeLiveData(): LiveData<String> = currentTrackTimeLiveData

    private val favoriteTrack = MutableLiveData<Track>()
    fun getFavoriteTrack(): LiveData<Track> = favoriteTrack

    private val isFavoriteTrack = MutableLiveData<Track?>()
    fun getIsFavoriteTrack(): LiveData<Track?> = isFavoriteTrack

    override fun onCleared() {
        playerStateLiveData.postValue(PlayerState.StateDefault)
        mediaPlayer.release()
        super.onCleared()
    }

    fun preparePlayer(url: String) {
        viewModelScope.launch(Dispatchers.IO) {
            mediaPlayer.setDataSource(url)
            mediaPlayer.prepareAsync()
        }

        mediaPlayer.setOnPreparedListener {
            playerStateLiveData.postValue(PlayerState.StatePrepared)
        }

        mediaPlayer.setOnCompletionListener {
            timerJob?.cancel()
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
        mediaPlayer.pause()
        timerJob?.cancel()
        playerStateLiveData.postValue(PlayerState.StatePaused)
    }

    fun onFavoriteClicked(track: Track) {
        val isFavorite = track.isFavorite

        viewModelScope.launch {
            when (isFavorite) {
                false -> favoriteTracksInteractor.insertTrack(track)
                true  -> favoriteTracksInteractor.deleteTrack(track)
            }
            track.isFavorite = !isFavorite
            favoriteTrack.postValue(track)
        }
    }

    private fun updatePlaybackTime() {
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(UPDATE_TIME_INTERVAL)
                val trackPosition = dateFormat.format(mediaPlayer.currentPosition)
                currentTrackTimeLiveData.postValue(trackPosition)
            }
        }
    }

    companion object {
        private const val UPDATE_TIME_INTERVAL = 300L
    }
}