package com.example.playlistmaker.player.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.core.model.Track
import com.example.playlistmaker.player.domain.PlayerState
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.util.dpToPx
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlayerActivity : AppCompatActivity() {
    private val playerViewModel: PlayerViewModel by viewModel()

    private lateinit var track: Track
    private lateinit var arrowBackButton: ImageButton
    private lateinit var albumImage: ImageView
    private lateinit var trackName: TextView
    private lateinit var artistName: TextView
    private lateinit var addButton: ImageButton
    private lateinit var playButton: ImageButton
    private lateinit var pauseButton: ImageButton
    private lateinit var likeButton: ImageButton
    private lateinit var playbackTime: TextView
    private lateinit var duration: TextView
    private lateinit var albumName: TextView
    private lateinit var albumYear: TextView
    private lateinit var genre: TextView
    private lateinit var country: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        // Подгружаем трек, переданный через Intent
        track = intent.getSerializableExtra(INTENT_NAME) as Track

        arrowBackButton = findViewById(R.id.arrow_back_player)
        albumImage = findViewById(R.id.album_image)
        trackName = findViewById(R.id.track_name)
        artistName = findViewById(R.id.artist_name)
        addButton = findViewById(R.id.add_playlist_button)
        playButton = findViewById(R.id.play_button)
        pauseButton = findViewById(R.id.pause_button)
        likeButton = findViewById(R.id.favorites_button)
        playbackTime = findViewById(R.id.playback_time)
        duration = findViewById(R.id.duration_value)
        albumName = findViewById(R.id.album_value)
        albumYear = findViewById(R.id.year_value)
        genre = findViewById(R.id.genre_value)
        country = findViewById(R.id.country_value)

        playerViewModel.getPlayerStateLiveData().observe(this) { playerState ->
            when (playerState) {
                PlayerState.StateDefault -> Log.i("PlayerState", "Default")
                PlayerState.StatePrepared -> prepareViewForPlayback()
                PlayerState.StatePlaying -> prepareViewForStartPlayer()
                PlayerState.StatePaused -> prepareViewForPausePlayer()
            }
        }

        playerViewModel.getCurrentTrackTimeLiveData().observe(this) { time ->
            updateCurrentTrackTime(time)
        }

        playerViewModel.preparePlayer(track.previewUrl)

        arrowBackButton.setOnClickListener {
            finish()
        }

        playButton.setOnClickListener {
            playerViewModel.playbackControl()
        }

        pauseButton.setOnClickListener {
            playerViewModel.playbackControl()
        }

        populatePlayerActivityViews()
    }

    override fun onPause() {
        super.onPause()
        playerViewModel.pausePlayer()
    }

    private fun populatePlayerActivityViews() {
        trackName.text = track.trackName
        artistName.text = track.artistName
        playbackTime.text = START_TIME
        duration.text = track.trackTime
        albumName.text = track.collectionName
        albumYear.text = playerViewModel.extractYear(track.releaseDate)
        genre.text = track.primaryGenreName
        country.text = track.country

        val imageLink = track.artworkUrl100.replaceAfterLast("/", "512x512bb.jpg")
        Glide.with(albumImage)
            .load(imageLink)
            .transform(RoundedCorners(dpToPx(8f, this@PlayerActivity)))
            .placeholder(R.drawable.player_placeholder)
            .into(albumImage)
    }

    private fun prepareViewForStartPlayer() {
        playButton.visibility = View.INVISIBLE
        pauseButton.visibility = View.VISIBLE
    }

    private fun prepareViewForPausePlayer() {
        playButton.visibility = View.VISIBLE
        pauseButton.visibility = View.INVISIBLE
    }

    private fun prepareViewForPlayback() {
        playButton.isEnabled = true
        playButton.visibility = View.VISIBLE
        pauseButton.visibility = View.INVISIBLE
        playbackTime.text = START_TIME
    }

    private fun updateCurrentTrackTime(time: String) {
        playbackTime.text = time
    }

    companion object {
        private const val INTENT_NAME = "Track"
        private const val START_TIME = "00:00"
    }
}