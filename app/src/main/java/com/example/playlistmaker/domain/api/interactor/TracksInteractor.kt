package com.example.playlistmaker.domain.api.interactor

import com.example.playlistmaker.domain.models.Track

interface TracksInteractor {
    fun searchTracks(searchText: String, consumer: TracksConsumer)

    interface TracksConsumer {
        fun consume(foundTracks: List<Track>?)
    }
}