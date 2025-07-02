package com.example.playlistmaker.db.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.playlistmaker.db.data.entity.TrackEntity

@Dao
interface TrackDao {

    @Query("SELECT * FROM track_table ORDER BY added_at DESC")
    suspend fun getAllTracks(): List<TrackEntity>

    @Query("SELECT track_id FROM track_table")
    suspend fun getAllTracksId(): List<Int>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(trackEntity: TrackEntity)

    @Delete
    suspend fun deleteTrack(trackEntity: TrackEntity)

}