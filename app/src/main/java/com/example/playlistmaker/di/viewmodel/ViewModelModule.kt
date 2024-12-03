package com.example.playlistmaker.di.viewmodel

import com.example.playlistmaker.main.ui.viewmodel.MainViewModel
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {

    viewModel {
        MainViewModel()
    }

    viewModel {
        SearchViewModel(get(), get())
    }

    viewModel {
        PlayerViewModel()
    }

    viewModel {
        SettingsViewModel(get(), get())
    }

}