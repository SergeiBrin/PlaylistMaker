package com.example.playlistmaker.db.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.playlistmaker.db.data.entity.PlaylistEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Query("SELECT * FROM playlist_table WHERE id = :playlistId")
    suspend fun getPlaylistById(playlistId: Int): PlaylistEntity?

    @Query("SELECT * FROM playlist_table")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylist(playlistEntity: PlaylistEntity)

    @Update
    suspend fun addTrackToPlaylist(playlistEntity: PlaylistEntity)

}