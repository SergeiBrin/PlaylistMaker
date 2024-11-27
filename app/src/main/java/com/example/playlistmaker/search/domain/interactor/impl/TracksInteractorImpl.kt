package com.example.playlistmaker.search.domain.interactor.impl

import com.example.playlistmaker.search.domain.interactor.api.TracksInteractor
import com.example.playlistmaker.search.domain.repository.TracksRepository
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