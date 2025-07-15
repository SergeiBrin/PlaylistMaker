package com.example.playlistmaker.db.data.repository

import com.example.playlistmaker.core.model.Playlist
import com.example.playlistmaker.core.model.Track
import com.example.playlistmaker.db.data.AppDatabase
import com.example.playlistmaker.db.data.converters.PlaylistDbConverter
import com.example.playlistmaker.db.data.converters.PlaylistTrackConverter
import com.example.playlistmaker.db.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class PlaylistRepositoryImpl(
    val dataBase: AppDatabase,
    val playlistConverter: PlaylistDbConverter,
    val playlistTrackConverter: PlaylistTrackConverter
) : PlaylistRepository {

    override fun getAllPlaylists(): Flow<List<Playlist>>  = flow {
        val playlistEntities = dataBase.playlistDao().getAllPlaylists()
        val playlists = playlistConverter.map(playlistEntities)
        emit(playlists)
    }

    override suspend fun insertPlaylist(playlist: Playlist) {
        val playlistEntity = playlistConverter.map(playlist)
        dataBase.playlistDao().insertPlaylist(playlistEntity)
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        val playlistEntity = playlistConverter.mapForUpdate(playlist, track.trackId)
        val playlistTrackEntity = playlistTrackConverter.map(track)

        dataBase.playListTrackDao().insertPlaylistTrack(playlistTrackEntity)
        dataBase.playlistDao().addTrackToPlaylist(playlistEntity)
    }
}