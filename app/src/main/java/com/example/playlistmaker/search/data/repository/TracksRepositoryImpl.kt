package com.example.playlistmaker.search.data.repository

import com.example.playlistmaker.search.data.network.NetworkClient
import com.example.playlistmaker.search.data.dto.TrackRequest
import com.example.playlistmaker.search.data.dto.TrackResponse
import com.example.playlistmaker.search.domain.repository.TracksRepository
import com.example.playlistmaker.core.model.Track

class TracksRepositoryImpl(
    private val networkClient: NetworkClient
) : TracksRepository {
    override fun searchTracks(searchText: String): List<Track>? {
        val resp = networkClient.doRequest(TrackRequest(searchText))

        if (resp.resultCode == 200) {
            return (resp as TrackResponse).tracks.map {
                Track(
                    it.trackId,
                    it.trackName,
                    it.artistName,
                    it.trackTime,
                    it.artworkUrl100,
                    it.collectionName,
                    it.releaseDate,
                    it.primaryGenreName,
                    it.country,
                    it.previewUrl
                )
            }
        } else {
            return null
        }
    }
}