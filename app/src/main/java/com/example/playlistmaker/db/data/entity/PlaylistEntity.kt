package com.example.playlistmaker.db.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlist_table")
data class PlaylistEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Int,

    @ColumnInfo(name = "playlist_name")
    val playlistName: String,

    @ColumnInfo(name = "playlist_description")
    val playlistDescription: String,

    @ColumnInfo(name = "playlist_image_uri")
    val playlistImageUri: String?,

    @ColumnInfo(name = "track_ids")
    val trackIds: String,

    @ColumnInfo(name = "track_count")
    val trackCount: Int
)