package com.example.playlistmaker.db.data.converters

import androidx.core.net.toUri
import com.example.playlistmaker.core.model.Playlist
import com.example.playlistmaker.db.data.entity.PlaylistEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class PlaylistDbConverter(
    private val gson: Gson
) {
    private val type = object : TypeToken<MutableList<Int>>() {}.type

    fun map(playlist: Playlist): PlaylistEntity {
        return PlaylistEntity(
            id = playlist.id,
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            playlistImageUri = playlist.playlistImageUri?.path,
            trackIds = gson.toJson(playlist.trackIds),
            trackCount = playlist.trackCount
        )
    }

    fun mapForUpdate(playlist: Playlist, trackId: Int): PlaylistEntity {
        playlist.trackIds.add(trackId)

        return PlaylistEntity(
            id = playlist.id,
            playlistName = playlist.playlistName,
            playlistDescription = playlist.playlistDescription,
            playlistImageUri = playlist.playlistImageUri?.path,
            trackIds = gson.toJson(playlist.trackIds),
            trackCount = playlist.trackCount + 1
        )
    }

    fun map(playlistEntity: PlaylistEntity): Playlist {
        return Playlist(
            id = playlistEntity.id,
            playlistName = playlistEntity.playlistName,
            playlistDescription = playlistEntity.playlistDescription,
            playlistImageUri = playlistEntity.playlistImageUri?.toUri(),
            trackIds = gson.fromJson(playlistEntity.trackIds, type),
            trackCount = playlistEntity.trackCount
        )
    }

    fun map(playlists: List<PlaylistEntity>): List<Playlist> {
        return playlists.map {
            map(it)
        }
    }
}