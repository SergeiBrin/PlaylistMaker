package com.example.playlistmaker.player.ui.adapter

import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.core.model.Playlist
import com.example.playlistmaker.utils.dpToPx
import java.io.File

class PlayerPlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val filePath = File(itemView.context
        .getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist-album")

    val playListImage = itemView.findViewById<ImageView>(R.id.playlist_image)
    val playlistName = itemView.findViewById<TextView>(R.id.playlist_name)
    val tracksCount = itemView.findViewById<TextView>(R.id.tracks_count)

    fun bind(model: Playlist) {
        playlistName.text = model.playlistName
        tracksCount.text = itemView.context.getString(R.string.tracks_count, model.trackCount)

        val file = File(filePath, "${model.playlistName}.jpg")
        Glide.with(itemView)
            .load(file)
            .transform(
                CenterCrop(),
                RoundedCorners(dpToPx(8f, itemView.context))
            )
            .placeholder(R.drawable.playlists_placeholder)
            .into(playListImage)
    }
}