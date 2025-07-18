package com.example.playlistmaker.player.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.core.model.Playlist
import com.example.playlistmaker.player.ui.funinterface.PlaylistClickListener

class PlayerPlaylistAdapter(
    val playlists: List<Playlist>,
    val clickListener: PlaylistClickListener
): RecyclerView.Adapter<PlayerPlaylistViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerPlaylistViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_player_playlist, parent, false)
        return PlayerPlaylistViewHolder(view)
    }

    override fun getItemCount(): Int {
        return playlists.size
    }

    override fun onBindViewHolder(holder: PlayerPlaylistViewHolder, position: Int) {
        holder.bind(playlists[position])

        holder.itemView.setOnClickListener {
            clickListener.onPlaylistClick(playlists[position])

        }
    }
}