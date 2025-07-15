package com.example.playlistmaker.search.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.core.model.Track
import com.example.playlistmaker.search.ui.funinterface.TrackClickListener

class TrackSearchHistoryAdapter(
    private var historyTracks: List<Track>,
    private val clickListener: TrackClickListener
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