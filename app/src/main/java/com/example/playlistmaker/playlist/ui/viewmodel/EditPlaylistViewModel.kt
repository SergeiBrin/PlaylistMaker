package com.example.playlistmaker.playlist.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.playlistmaker.core.util.Result
import com.example.playlistmaker.db.domain.interactor.PlaylistInteractor
import kotlinx.coroutines.launch

class EditPlaylistViewModel(
    playlistInteractor: PlaylistInteractor
) : CreatePlaylistViewModel(playlistInteractor) {

    private val updatePlaylistLiveData = MutableLiveData<Result>()
    fun getUpdatePlaylistLiveData(): LiveData<Result> = updatePlaylistLiveData

    fun updatePlaylist(
        id: Int,
        playlistName: String,
        playlistDescription: String,
        playlistImageUri: Uri?
    ) {
        viewModelScope.launch {
            val updateCount = playlistInteractor
                .updatePlaylist(
                    id,
                    playlistName,
                    playlistDescription,
                    playlistImageUri
                )

            if (updateCount != 0) {
                updatePlaylistLiveData.postValue(Result.Success)
            } else {
                updatePlaylistLiveData.postValue(Result.Failure)
            }
        }
    }
}