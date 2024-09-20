package com.example.playlistmaker.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.activity.PlayerActivity
import com.example.playlistmaker.model.Track
import com.example.playlistmaker.sharedpref.SearchHistory

class TracksAdapter(
    val context: Context,
    val searchHistory: SearchHistory,
    val tracks: List<Track>
) : RecyclerView.Adapter<TracksViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        return TracksViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tracks.size
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        val track = tracks[position]
        holder.bind(track)

        holder.itemView.setOnClickListener {
            searchHistory.saveTrackInHistoryTrackList(track)
            val playerIntent = Intent(context, PlayerActivity::class.java).apply {
                putExtra("Track", track)
            }

            context.startActivity(playerIntent)
        }

    }

}