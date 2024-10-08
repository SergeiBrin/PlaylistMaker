package com.example.playlistmaker.activity

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.model.Track
import com.example.playlistmaker.util.dpToPx
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class PlayerActivity : AppCompatActivity() {
    lateinit var track: Track
    lateinit var arrowBackButton: ImageButton
    lateinit var albumImage: ImageView
    lateinit var trackName: TextView
    lateinit var artistName: TextView
    lateinit var addButton: ImageButton
    lateinit var playButton: ImageButton
    lateinit var likeButton: ImageButton
    lateinit var playbackTime: TextView
    lateinit var duration: TextView
    lateinit var albumName: TextView
    lateinit var albumYear: TextView
    lateinit var genre: TextView
    lateinit var country: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        // Подгружаем переданный через Intent трек
        track = intent.getSerializableExtra("Track") as Track

        arrowBackButton = findViewById(R.id.arrow_back_player)
        albumImage = findViewById(R.id.album_image)
        trackName = findViewById(R.id.track_name)
        artistName = findViewById(R.id.artist_name)
        addButton = findViewById(R.id.add_playlist_button)
        playButton = findViewById(R.id.playback_control_button)
        likeButton = findViewById(R.id.favorites_button)
        playbackTime = findViewById(R.id.playback_time)
        duration = findViewById(R.id.duration_value)
        albumName = findViewById(R.id.album_value)
        albumYear = findViewById(R.id.year_value)
        genre = findViewById(R.id.genre_value)
        country = findViewById(R.id.country_value)

        arrowBackButton.setOnClickListener {
            finish()
        }

        populatePlayerActivityViews()
    }

    private fun populatePlayerActivityViews() {
        trackName.text = track.trackName
        artistName.text = track.artistName
        playbackTime.text = "0:00"
        duration.text = track.trackTime
        albumName.text = track.collectionName
        albumYear.text = extractYear()
        genre.text = track.primaryGenreName
        country.text = track.country

        val imageLink = track.artworkUrl100.replaceAfterLast("/", "512x512bb.jpg")
        Glide.with(albumImage)
            .load(imageLink)
            .transform(RoundedCorners(dpToPx(8f, this@PlayerActivity)))
            .placeholder(R.drawable.player_placeholder)
            .into(albumImage)
    }

    private fun extractYear(): String {
        var year = ""

        try {
            val zonedDateTime = ZonedDateTime.parse(track.releaseDate, DateTimeFormatter.ISO_ZONED_DATE_TIME)
            year = zonedDateTime.year.toString()
        } catch (e: DateTimeParseException) {
            Log.i("Info","Не удалось преобразовать строку releaseDate в ZonedDateTime")
        }

        return year
    }
}