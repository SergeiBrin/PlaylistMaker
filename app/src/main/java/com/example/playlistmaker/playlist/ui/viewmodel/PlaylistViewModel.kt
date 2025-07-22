package com.example.playlistmaker.playlist.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.core.util.Result
import com.example.playlistmaker.db.domain.interactor.PlaylistInteractor
import com.example.playlistmaker.playlist.ui.result.PlaylistUiResult
import kotlinx.coroutines.launch

class PlaylistViewModel(
    val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val playlistResultLiveData = MutableLiveData<PlaylistUiResult>()
    fun getPlaylistLiveData(): LiveData<PlaylistUiResult> = playlistResultLiveData

    private val playlistDeletedLiveData = MutableLiveData<Result>()
    fun getPlaylistDeletedLiveData(): LiveData<Result> = playlistDeletedLiveData

    fun getPlaylistById(playlistId: Int) {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(playlistId)

            playlist?.let {
                playlistInteractor.getPlaylistTracksByIds(playlist.trackIds).collect {
                    playlistResultLiveData.postValue(PlaylistUiResult(playlist, it))
                }
            }
        }
    }

    fun deleteTrackFromPlaylist(playlistId: Int, trackId: Int) {
        viewModelScope.launch {
            val deleteCount = playlistInteractor.deleteTrackFromPlaylist(playlistId, trackId)
            getPlaylistById(playlistId)

            if (deleteCount != 0) playlistInteractor.deleteTrackIfUnused(trackId)
        }
    }

    fun deletePlaylist(playlistId: Int) {
        viewModelScope.launch {
            val playlist = playlistInteractor.getPlaylistById(playlistId)
            if (playlist != null) {
                val trackIds = playlist.trackIds
                val deleteCount = playlistInteractor.deletePlaylistById(playlistId)

                if (deleteCount != 0) {
                    playlistInteractor.deleteTracksIfUnused(trackIds)
                    playlistDeletedLiveData.postValue(Result.Success)
                }
            } else {
                playlistDeletedLiveData.postValue(Result.Failure)
            }
        }
    }
}