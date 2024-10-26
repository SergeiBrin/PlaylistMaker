package com.example.playlistmaker.domain.impl

import com.example.playlistmaker.domain.api.interactor.TracksInteractor
import com.example.playlistmaker.domain.api.repository.TracksRepository
import java.util.concurrent.Executors

class TracksInteractorImpl(
    private val tracksRepository: TracksRepository
) : TracksInteractor {
    private val executor = Executors.newCachedThreadPool()

    override fun searchTracks(searchText: String, consumer: TracksInteractor.TracksConsumer) {
        executor.execute {
            consumer.consume(tracksRepository.searchTracks(searchText))
        }
    }
}