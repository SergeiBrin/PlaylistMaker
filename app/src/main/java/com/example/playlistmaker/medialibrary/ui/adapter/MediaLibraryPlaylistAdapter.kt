package com.example.playlistmaker.medialibrary.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.core.model.Playlist
import com.example.playlistmaker.player.ui.funinterface.PlaylistClickListener

class MediaLibraryPlaylistAdapter(
    val playlists: List<Playlist>,
    val clickListener: PlaylistClickListener
): RecyclerView.Adapter<MediaLibraryPlaylistViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MediaLibraryPlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_media_library_playlist, parent, false)
        return MediaLibraryPlaylistViewHolder(view)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: MediaLibraryPlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])

        holder.itemView.setOnClickListener {
            clickListener.onPlaylistClick(playlists[position])
        }
    }
}