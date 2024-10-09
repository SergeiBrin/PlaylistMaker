package com.example.playlistmaker.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.listeners.TrackClickListener
import com.example.playlistmaker.model.Track

class TrackSearchHistoryAdapter(
    val historyTracks: List<Track>,
    val clickListener: TrackClickListener
) : RecyclerView.Adapter<TracksViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TracksViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_track, parent, false)
        return TracksViewHolder(view)
    }

    override fun getItemCount(): Int {
        return historyTracks.size
    }

    override fun onBindViewHolder(holder: TracksViewHolder, position: Int) {
        val track = historyTracks[position]
        holder.bind(track)

        holder.itemView.setOnClickListener {
            clickListener.onTrackClick(track)
        }
    }
}