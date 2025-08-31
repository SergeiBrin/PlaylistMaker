package com.example.playlistmaker.player.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.core.model.Playlist
import com.example.playlistmaker.core.model.Track
import com.example.playlistmaker.db.domain.interactor.FavoriteTracksInteractor
import com.example.playlistmaker.db.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.player.ui.result.AddTrackInPlaylistResult
import com.example.playlistmaker.player.ui.result.PlayerState
import com.example.playlistmaker.player.ui.result.PlayerUiState
import com.example.playlistmaker.player.ui.service.AudioPlayerControl
import kotlinx.coroutines.launch

class PlayerViewModel(
    private val favoriteTracksInteractor: FavoriteTracksInteractor,
    private val playlistInteractor: PlaylistInteractor,
) : ViewModel() {
    private var audioPlayerControl: AudioPlayerControl? = null

    private val playerStateLiveData = MutableLiveData<PlayerState>(PlayerState.StateDefault())
    fun getPlayerStateLiveData(): LiveData<PlayerState> = playerStateLiveData

    private val playerUiStateLiveData = MutableLiveData<PlayerUiState>()
    fun getPlayerUiStateLiveData(): LiveData<PlayerUiState> = playerUiStateLiveData

    private val playlistsLiveData = MutableLiveData<List<Playlist>>()
    fun getPlaylistsLiveData(): LiveData<List<Playlist>> = playlistsLiveData

    private val addTrackToPlaylist = MutableLiveData<AddTrackInPlaylistResult>()
    fun getAddTrackToPlaylist(): LiveData<AddTrackInPlaylistResult> = addTrackToPlaylist

    override fun onCleared() {
        playerStateLiveData.postValue(PlayerState.StateDefault())
        super.onCleared()
    }

    fun setAudioPlayerControl(audioPlayerControl: AudioPlayerControl) {
        this.audioPlayerControl = audioPlayerControl

        viewModelScope.launch {
            audioPlayerControl.getPlayerState().collect {
                playerStateLiveData.postValue(it)
            }
        }
    }

    fun removeAudioPlayerControl() {
        audioPlayerControl = null
    }

    fun playbackControl() {
        val playerState = playerStateLiveData.value

        when(playerState) {
            is PlayerState.StatePlaying -> audioPlayerControl?.pause()
            is PlayerState.StatePrepared, is PlayerState.StatePaused -> audioPlayerControl?.play()
            else -> Log.i("INFO", "Media player is in default state")
        }
    }

    fun startForeground() {
        audioPlayerControl?.startForeground()
    }

    fun stopForeground() {
        audioPlayerControl?.stopForeground()
    }

    fun onFavoriteClicked(track: Track) {
        val isFavorite = track.isFavorite

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
            val track = favoriteTracksInteractor.getTrackById(trackId)
            if (track != null) {
                playerUiStateLiveData.postValue(PlayerUiState.FavoriteTrackSuccessEvent)
            } else {
                playerUiStateLiveData.postValue(PlayerUiState.FavoriteTrackFailureEvent)
            }
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

    companion object {
        private const val UPDATE_TIME_INTERVAL = 300L
    }
}