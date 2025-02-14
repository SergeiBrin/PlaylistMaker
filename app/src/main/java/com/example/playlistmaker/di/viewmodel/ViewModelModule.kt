package com.example.playlistmaker.di.viewmodel

import com.example.playlistmaker.medialibrary.ui.viewholder.LikedSongsViewModel
import com.example.playlistmaker.medialibrary.ui.viewholder.PlaylistsViewModel
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel {
        PlayerViewModel()
    }

    viewModel {
        SettingsViewModel(get())
    }

    viewModel {
        LikedSongsViewModel()
    }

    viewModel {
        PlaylistsViewModel()
    }

}