package com.example.playlistmaker.db.data.converters

import com.example.playlistmaker.core.model.Track
import com.example.playlistmaker.db.data.entity.PlaylistTrackEntity

class PlaylistTrackConverter {
    fun map(track: Track): PlaylistTrackEntity {
        return PlaylistTrackEntity(
            trackId = track.trackId,
            trackName = track.trackName,
            artistName = track.artistName,
            trackTime = track.trackTime,
            artworkUrl100 = track.artworkUrl100,
            collectionName = track.collectionName,
            releaseDate = track.releaseDate,
            primaryGenreName = track.primaryGenreName,
            country = track.country,
            previewUrl = track.previewUrl
        )
    }

    fun map(playlistTrack: PlaylistTrackEntity): Track {
        return Track(
            trackId = playlistTrack.trackId,
            trackName = playlistTrack.trackName,
            artistName = playlistTrack.artistName,
            trackTime = playlistTrack.trackTime,
            artworkUrl100 = playlistTrack.artworkUrl100,
            collectionName = playlistTrack.collectionName,
            releaseDate = playlistTrack.releaseDate,
            primaryGenreName = playlistTrack.primaryGenreName,
            country = playlistTrack.country,
            previewUrl = playlistTrack.previewUrl
        )
    }

    fun map(tracks: List<PlaylistTrackEntity>): List<Track> {
        return tracks.map {
            map(it)
        }
    }
}