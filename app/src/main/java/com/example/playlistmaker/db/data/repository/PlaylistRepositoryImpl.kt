package com.example.playlistmaker.db.data.repository

import android.net.Uri
import com.example.playlistmaker.core.model.Playlist
import com.example.playlistmaker.core.model.Track
import com.example.playlistmaker.db.data.AppDatabase
import com.example.playlistmaker.db.data.converters.PlaylistDbConverter
import com.example.playlistmaker.db.data.converters.PlaylistTrackConverter
import com.example.playlistmaker.db.domain.repository.PlaylistRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class PlaylistRepositoryImpl(
    val dataBase: AppDatabase,
    val playlistConverter: PlaylistDbConverter,
    val playlistTrackConverter: PlaylistTrackConverter
) : PlaylistRepository {

    override suspend fun getPlaylistById(playlistId: Int): Playlist? {
        val playlistEntity = dataBase.playlistDao().getPlaylistById(playlistId)
        return playlistEntity?.let { playlistConverter.map(it) }
    }

    override suspend fun getAllPlaylists(): Flow<List<Playlist>> {
        return dataBase
            .playlistDao()
            .getAllPlaylists()
            .map {
                entities -> playlistConverter.map(entities)
            }
    }

    override suspend fun getAllPlaylistsOnce(): List<Playlist> {
        val playlists = dataBase.playlistDao().getAllPlaylistsOnce()
        return playlistConverter.map(playlists)
    }


    override suspend fun insertPlaylist(playlist: Playlist): Long {
        val playlistEntity = playlistConverter.map(playlist)
        return dataBase.playlistDao().insertPlaylist(playlistEntity)
    }

    override suspend fun addTrackToPlaylist(playlist: Playlist, track: Track) {
        val playlistEntity = playlistConverter.mapForUpdate(playlist, track.trackId)
        val playlistTrackEntity = playlistTrackConverter.map(track)

        dataBase.playListTrackDao().insertPlaylistTrack(playlistTrackEntity)
        dataBase.playlistDao().updateTracksToPlaylist(playlistEntity)
    }

    override suspend fun updatePlaylist(
        id: Int,
        playlistName: String,
        playlistDescription: String,
        playlistImageUri: Uri?
    ): Int {
        val uriPath = playlistImageUri?.path
        return dataBase.playlistDao().updatePlaylist(id, playlistName, playlistDescription, uriPath)
    }

    override suspend fun getPlaylistTracksByIds(ids: List<Int>): Flow<List<Track>> {
        return dataBase
            .playListTrackDao()
            .getTracksByIds(ids)
            .map {
                playlistTrackConverter.map(it)
            }
    }

    override suspend fun deleteTrackFromPlaylist(playlist: Playlist): Int {
        val playlistEntity = playlistConverter.mapForDelete(playlist)
        return dataBase.playlistDao().updateTracksToPlaylist(playlistEntity)
    }

    override suspend fun deletePlaylistTrackById(trackId: Int) {
        dataBase.playListTrackDao().deletePlaylistTrack(trackId)
    }

    override suspend fun deletePlaylistTracksByIds(trackIds: List<Int>) {
        dataBase.playListTrackDao().deletePlaylistTracksByIds(trackIds)
    }

    override suspend fun deletePlaylistById(playlistId: Int): Int {
        return dataBase.playlistDao().deletePlaylistById(playlistId)
    }
}