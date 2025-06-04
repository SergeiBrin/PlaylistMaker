package com.example.playlistmaker.di.data

import android.content.Context.MODE_PRIVATE
import com.example.playlistmaker.search.data.deserializer.TrackDeserializer
import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.data.history.SearchHistory
import com.example.playlistmaker.search.data.network.ItunesApiService
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.utils.createRetrofit
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

const val URL = "https://itunes.apple.com"
const val SETTINGS_KEY = "PlaylistMakerSettings"
const val SEARCH_HISTORY_KEY = "SearchHistory"

val dataModule = module {
    single<ItunesApiService> {
        createRetrofit(
            URL,
            GsonBuilder()
                .registerTypeAdapter(TrackDto::class.java, TrackDeserializer())
                .create()
        )
            .create(ItunesApiService::class.java)
    }

    single(named(SETTINGS_KEY)) {
        androidContext()
            .getSharedPreferences(SETTINGS_KEY, MODE_PRIVATE)
    }

    single(named(SEARCH_HISTORY_KEY)) {
        androidContext()
            .getSharedPreferences(SEARCH_HISTORY_KEY, MODE_PRIVATE)
    }

    factory {
        Gson()
    }

    single<NetworkClient> {
        RetrofitNetworkClient(get())
    }

    single {
        SearchHistory(get(named("SearchHistory")), get())
    }
}