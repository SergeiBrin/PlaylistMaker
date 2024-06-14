package com.example.playlistmaker.adapter

import android.view.RoundedCorner
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.model.Track

class TracksViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
    private val trackName = itemView.findViewById<TextView>(R.id.track_name)
    private val artistName = itemView.findViewById<TextView>(R.id.artist_name)
    private val trackTime = itemView.findViewById<TextView>(R.id.track_time)
    private var artworkUrl100 = itemView.findViewById<ImageView>(R.id.track_cover)

    fun bind(model: Track) {
        trackName.text = model.trackName
        artistName.text = model.artistName
        trackTime.text = model.trackTime
        Glide.with(itemView)
            .load(model.artworkUrl100)
            .transform(RoundedCorners(8))
            .placeholder(R.drawable.placeholder)
            .into(artworkUrl100)
    }
}