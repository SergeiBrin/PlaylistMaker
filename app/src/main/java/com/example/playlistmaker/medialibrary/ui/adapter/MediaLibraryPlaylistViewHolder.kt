package com.example.playlistmaker.medialibrary.ui.adapter

import android.graphics.drawable.Drawable
import android.os.Environment
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.playlistmaker.R
import com.example.playlistmaker.core.model.Playlist
import com.example.playlistmaker.utils.dpToPx
import java.io.File

class MediaLibraryPlaylistViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val filePath = File(itemView.context
        .getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist-album")

    val playListImage = itemView.findViewById<ImageView>(R.id.my_playlist_image)
    val placeholder = itemView.findViewById<ImageView>(R.id.playlist_placeholder)
    val playlistName = itemView.findViewById<TextView>(R.id.my_playlist_name)
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
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    placeholder.isVisible = true
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    placeholder.isVisible = false
                    return false
                }
            })
            .into(playListImage)
    }
}