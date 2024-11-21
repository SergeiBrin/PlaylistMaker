package com.example.playlistmaker.search.domain.interactor.api

import com.example.playlistmaker.core.model.Track

interface TracksInteractor {
    fun searchTracks(searchText: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?)
    }
}