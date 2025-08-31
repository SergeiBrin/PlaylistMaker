package com.example.playlistmaker.di.viewmodel

import com.example.playlistmaker.medialibrary.ui.viewmodel.LikedSongsViewModel
import com.example.playlistmaker.medialibrary.ui.viewmodel.PlaylistsViewModel
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.playlist.ui.viewmodel.CreatePlaylistViewModel
import com.example.playlistmaker.playlist.ui.viewmodel.EditPlaylistViewModel
import com.example.playlistmaker.playlist.ui.viewmodel.PlaylistViewModel
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel {
        PlayerViewModel(get(), get())
    }

    viewModel {
        SettingsViewModel(get())
    }

    viewModel {
        LikedSongsViewModel(get())
    }

    viewModel {
        PlaylistsViewModel(get())
    }

    viewModel {
        PlaylistViewModel(get())
    }

    viewModel {
        CreatePlaylistViewModel(get())
    }

    viewModel {
        EditPlaylistViewModel(get())
    }

}