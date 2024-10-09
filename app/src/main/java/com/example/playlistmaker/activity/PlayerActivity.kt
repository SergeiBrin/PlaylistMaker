package com.example.playlistmaker.activity

import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.model.Track
import com.example.playlistmaker.util.dpToPx
import java.text.SimpleDateFormat
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

class PlayerActivity : AppCompatActivity() {
    private var mediaPlayer = MediaPlayer()
    private val handler = Handler(Looper.getMainLooper())

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

    companion object {
        private const val STATE_DEFAULT = 0
        private const val STATE_PREPARED = 1
        private const val STATE_PLAYING = 2
        private const val STATE_PAUSED = 3
        private const val START_TIME = "00:00"
        private const val UPDATE_TIME_INTERVAL = 200L
    }

    private var playerState = STATE_DEFAULT


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        // Подгружаем переданный через Intent трек
        track = intent.getSerializableExtra("Track") as Track
        preparePlayer()

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

        arrowBackButton.setOnClickListener {
            finish()
        }

        playButton.setOnClickListener {
            playbackControl()
        }

        pauseButton.setOnClickListener {
            playbackControl()
        }

        populatePlayerActivityViews()
    }

    override fun onPause() {
        super.onPause()
        pausePlayer()
    }

    override fun onDestroy() {
        playerState = STATE_DEFAULT
        super.onDestroy()
        mediaPlayer.release()
    }

    private fun populatePlayerActivityViews() {
        trackName.text = track.trackName
        artistName.text = track.artistName
        playbackTime.text = START_TIME
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

    private fun preparePlayer() {
        val url = track.previewUrl

        mediaPlayer.setDataSource(url)
        mediaPlayer.prepareAsync()

        mediaPlayer.setOnPreparedListener {
            playButton.isEnabled = true
            playerState = STATE_PREPARED
        }

        mediaPlayer.setOnCompletionListener {
            playButton.visibility = View.VISIBLE
            pauseButton.visibility = View.INVISIBLE
            playerState = STATE_PREPARED

            playbackTime.text = START_TIME
        }
    }

    private fun startPlayer() {
        mediaPlayer.start()

        playButton.visibility = View.INVISIBLE
        pauseButton.visibility = View.VISIBLE
        playerState = STATE_PLAYING

        updatePlaybackTime()
    }

    private fun pausePlayer() {
        mediaPlayer.pause()

        playButton.visibility = View.VISIBLE
        pauseButton.visibility = View.INVISIBLE

        playerState = STATE_PAUSED
    }

    private fun playbackControl() {
        when(playerState) {
            STATE_PLAYING -> {
                pausePlayer()
            }
            STATE_PREPARED, STATE_PAUSED -> {
                startPlayer()
            }
        }
    }

    private fun updatePlaybackTime() {
        handler.post(object : Runnable {
            override fun run() {
                when (playerState) {
                    STATE_PLAYING -> {
                        playbackTime.text = SimpleDateFormat("mm:ss", Locale.getDefault())
                            .format(mediaPlayer.currentPosition)
                        handler.postDelayed(this, UPDATE_TIME_INTERVAL)
                    }

                    else -> handler.removeCallbacks(this)
                }
            }
        })
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