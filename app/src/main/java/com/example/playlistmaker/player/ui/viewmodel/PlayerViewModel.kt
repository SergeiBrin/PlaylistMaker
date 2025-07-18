package com.example.playlistmaker.player.ui.viewmodel

import android.media.MediaPlayer
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.core.model.Playlist
import com.example.playlistmaker.core.model.Track
import com.example.playlistmaker.db.domain.interactor.FavoriteTracksInteractor
import com.example.playlistmaker.db.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.player.domain.PlayerState
import com.example.playlistmaker.player.domain.PlayerUiState
import com.example.playlistmaker.player.ui.result.AddTrackInPlaylistResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class PlayerViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val playlistInteractor: PlaylistInteractor,
    private val mediaPlayer: MediaPlayer
) : ViewModel() {
    private var timerJob: Job? = null

    private val dateFormat by lazy { SimpleDateFormat("mm:ss", Locale.getDefault()) }

    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.StateDefault)
    fun getPlayerStateLiveData(): LiveData<PlayerState> = playerStateLiveData

    private val playerUiStateLiveData = MutableLiveData<PlayerUiState>()
    fun getPlayerUiStateLiveData(): LiveData<PlayerUiState> = playerUiStateLiveData

    private val playlistsLiveData = MutableLiveData<List<Playlist>>()
    fun getPlaylistsLiveData(): LiveData<List<Playlist>> = playlistsLiveData

    private val addTrackToPlaylist = MutableLiveData<AddTrackInPlaylistResult>()
    fun getAddTrackToPlaylist(): LiveData<AddTrackInPlaylistResult> = addTrackToPlaylist

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
        val isFavorite = track.isFavorite // false

        viewModelScope.launch {
            if (isFavorite) {
                favoriteTracksInteractor.deleteTrack(track)
            } else {
                favoriteTracksInteractor.insertTrack(track)
            }

            track.isFavorite = !isFavorite
            playerUiStateLiveData.postValue(PlayerUiState.TrackData(track))
        }
    }

    fun getTrackById(trackId: Int) {
        viewModelScope.launch {
            val result = favoriteTracksInteractor.getTrackById(trackId)
            playerUiStateLiveData.postValue(result)
        }
    }

    fun getAllPlaylists() {
        viewModelScope.launch {
            playlistInteractor.getAllPlaylists().collect {
                playlistsLiveData.postValue(it)
            }
        }
    }

    fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        val trackId = track.trackId
        val playlistId = playlist.id

        var isTrackInPlaylist: Boolean

        viewModelScope.launch {
            isTrackInPlaylist = playlistInteractor.isTrackInPlaylist(trackId, playlistId)
            if (isTrackInPlaylist) {
                addTrackToPlaylist
                    .postValue(
                        AddTrackInPlaylistResult
                            .Failure(playlist.playlistName)
                    )
            } else {
                playlistInteractor.addTrackToPlaylist(playlist, track)

                isTrackInPlaylist = playlistInteractor.isTrackInPlaylist(trackId, playlistId)
                if (isTrackInPlaylist) {
                    addTrackToPlaylist
                        .postValue(
                            AddTrackInPlaylistResult
                                .Success(playlist.playlistName)
                        )
                }
            }
        }
    }

    private fun updatePlaybackTime() {
        timerJob = viewModelScope.launch {
            while (mediaPlayer.isPlaying) {
                delay(UPDATE_TIME_INTERVAL)
                val trackPosition = dateFormat.format(mediaPlayer.currentPosition)
                playerUiStateLiveData.postValue(PlayerUiState.CurrentTrackTimeLiveData(trackPosition))
            }
        }
    }

    companion object {
        private const val UPDATE_TIME_INTERVAL = 300L
    }
}