package com.example.playlistmaker.di.data

import android.content.Context.MODE_PRIVATE
import com.example.playlistmaker.search.data.deserializer.TrackDeserializer
import com.example.playlistmaker.search.data.dto.TrackDto
import com.example.playlistmaker.search.data.history.SearchHistory
import com.example.playlistmaker.search.data.network.ItunesApiService
import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.network.RetrofitNetworkClient
import com.example.playlistmaker.util.createRetrofit
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val dataModule = module {
    single<ItunesApiService> {
        createRetrofit(
            "https://itunes.apple.com",
            GsonBuilder()
                .registerTypeAdapter(TrackDto::class.java, TrackDeserializer())
                .create()
        )
            .create(ItunesApiService::class.java)
    }

    single(named("PlaylistMakerSettings")) {
        androidContext()
            .getSharedPreferences("PlaylistMakerSettings", MODE_PRIVATE)
    }

    single(named("SearchHistory")) {
        androidContext()
            .getSharedPreferences("SearchHistory", MODE_PRIVATE)
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