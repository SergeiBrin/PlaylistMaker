package com.example.playlistmaker.db.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.db.data.entity.PlaylistTrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistTrackDao {

    @Query("SELECT * FROM playlist_track WHERE track_id IN (:ids) ORDER BY added_at DESC")
    fun getTracksByIds(ids: List<Int>): Flow<List<PlaylistTrackEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylistTrack(playlistTrack: PlaylistTrackEntity)

    @Query("DELETE FROM playlist_track WHERE track_id = :trackId")
    suspend fun deletePlaylistTrack(trackId: Int)

    @Query("DELETE FROM playlist_track WHERE track_id in (:trackIds)")
    suspend fun deletePlaylistTracksByIds(trackIds: List<Int>)

}