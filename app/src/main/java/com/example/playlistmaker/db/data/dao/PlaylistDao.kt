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

    @Query("SELECT * FROM playlist_table")
    suspend fun getAllPlaylistsOnce(): List<PlaylistEntity>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylist(playlistEntity: PlaylistEntity): Long

    @Update
    suspend fun updateTracksToPlaylist(playlistEntity: PlaylistEntity): Int

    @Query("""
        UPDATE playlist_table 
        SET playlist_name = :playlistName, 
            playlist_description = :playlistDescription,
            playlist_image_uri = :playlistImageUri
        WHERE id = :id
        """)
    suspend fun updatePlaylist(
        id: Int,
        playlistName: String,
        playlistDescription: String,
        playlistImageUri: String?
    ): Int

    @Query("DELETE FROM playlist_table WHERE id = :playlistId")
    suspend fun deletePlaylistById(playlistId: Int): Int

}