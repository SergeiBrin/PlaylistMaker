package com.example.playlistmaker.playlist.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.core.model.Playlist
import com.example.playlistmaker.core.util.Result
import com.example.playlistmaker.db.domain.interactor.PlaylistInteractor
import kotlinx.coroutines.launch

open class CreatePlaylistViewModel(
    protected val playlistInteractor: PlaylistInteractor
) : ViewModel() {

    private val createPlaylistLiveData = MutableLiveData<Result>()
    fun getCreatePlaylistLiveData(): LiveData<Result> = createPlaylistLiveData

    fun insertPlaylist(playlist: Playlist) {
        viewModelScope.launch {
            val createPlaylistCount = playlistInteractor.insertPlaylist(playlist)
            if (createPlaylistCount != -1L) {
                createPlaylistLiveData.postValue(Result.Success)
            } else {
                createPlaylistLiveData.postValue(Result.Failure)
            }
        }
    }
}