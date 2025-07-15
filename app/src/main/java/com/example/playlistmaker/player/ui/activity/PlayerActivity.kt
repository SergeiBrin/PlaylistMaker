package com.example.playlistmaker.player.ui.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.core.model.Playlist
import com.example.playlistmaker.core.model.Track
import com.example.playlistmaker.player.domain.PlayerState
import com.example.playlistmaker.player.ui.adapter.PlayerPlaylistAdapter
import com.example.playlistmaker.player.ui.result.AddTrackInPlaylistResult
import com.example.playlistmaker.player.ui.result.GetTrackResult
import com.example.playlistmaker.player.ui.viewmodel.PlayerViewModel
import com.example.playlistmaker.playlist.ui.fragment.CreatePlaylistFragment
import com.example.playlistmaker.utils.dpToPx
import com.example.playlistmaker.utils.extractYear
import com.google.android.material.bottomsheet.BottomSheetBehavior
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

    private lateinit var bottomSheet: LinearLayout
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var playlistAdapter: PlayerPlaylistAdapter
    private lateinit var playlistRecycleView: RecyclerView
    private lateinit var createPlaylist: Button

    private lateinit var createPlaylistFragmentContainer: FrameLayout
    private val playlists: MutableList<Playlist> = mutableListOf()

    private lateinit var overlay: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        // Подгружаем трек, переданный через Intent
        track = intent.getSerializableExtra(INTENT_TRACK_KEY) as Track

        arrowBackButton = findViewById(R.id.arrow_back_player)
        albumImage = findViewById(R.id.album_image)
        trackName = findViewById(R.id.track_name)
        artistName = findViewById(R.id.artist_name)
        addButton = findViewById(R.id.add_playlist_button)
        playButton = findViewById(R.id.play_button)
        pauseButton = findViewById(R.id.pause_button)
        likeButton = findViewById(R.id.like_button)
        playbackTime = findViewById(R.id.playback_time)
        duration = findViewById(R.id.duration_value)
        albumName = findViewById(R.id.album_value)
        albumYear = findViewById(R.id.year_value)
        genre = findViewById(R.id.genre_value)
        country = findViewById(R.id.country_value)

        bottomSheet = findViewById(R.id.bottom_sheet_container)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        createPlaylist = findViewById(R.id.create_playlist_button)
        playlistRecycleView = findViewById(R.id.bottom_sheet_recycle_view)
        playlistAdapter = PlayerPlaylistAdapter(playlists) { playlist ->
            playerViewModel.addTrackToPlaylist(playlist, track)
        }
        playlistRecycleView.adapter = playlistAdapter

        createPlaylistFragmentContainer = findViewById(R.id.create_playlist_fragment_container)

        overlay = findViewById(R.id.overlay)

        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                        overlay.visibility = View.GONE
                    }
                    else -> {
                        overlay.visibility = View.VISIBLE
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                val alpha = (slideOffset + 1).coerceIn(0f, 1f)
                overlay.alpha = alpha
            }

        })

        playerViewModel.getAddTrackToPlaylist().observe(this) { result ->
            when (result) {
                is AddTrackInPlaylistResult.Success -> {
                    bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
                }
                is AddTrackInPlaylistResult.Failure ->
                    Toast.makeText(this, result.message, Toast.LENGTH_LONG).show()
            }

        }

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

        playerViewModel.getTrackLiveData().observe(this) { track ->
            updateLikeButtonIcon(track)
         }

        playerViewModel.getPlaylistsLiveData().observe(this) {
            updatePlaylists(it)
        }

        playerViewModel.getFavoriteTrackLiveData().observe(this) { result ->
            when (result) {
                GetTrackResult.Success -> {
                    track.isFavorite = true
                    likeButton.setImageResource(R.drawable.like_active)
                }
                GetTrackResult.Failure -> {
                    track.isFavorite = false
                    likeButton.setImageResource(R.drawable.like_inactive)
                }
            }
        }

        playerViewModel.preparePlayer(track.previewUrl)

        playerViewModel.getTrackById(track.trackId)

        addButton.setOnClickListener {
            playerViewModel.getAllPlaylists()
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        createPlaylist.setOnClickListener {
            val createPlaylistFragment = CreatePlaylistFragment()

            supportFragmentManager
                .beginTransaction()
                .replace(createPlaylistFragmentContainer.id, createPlaylistFragment)
                .addToBackStack(null)
                .commit()

        }

        supportFragmentManager.setFragmentResultListener("playlist_added", this) { _, _ ->
            playerViewModel.getAllPlaylists()
        }

        arrowBackButton.setOnClickListener {
            finish()
        }

        playButton.setOnClickListener {
            playerViewModel.playbackControl()
        }

        pauseButton.setOnClickListener {
            playerViewModel.playbackControl()
        }

        likeButton.setOnClickListener {
            playerViewModel.onFavoriteClicked(track)
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
        albumYear.text = extractYear(track.releaseDate)
        genre.text = track.primaryGenreName
        country.text = track.country

        val imageLink = track.artworkUrl100.replaceAfterLast("/", "512x512bb.jpg")
        Glide.with(albumImage)
            .load(imageLink)
            .transform(RoundedCorners(dpToPx(8f, this@PlayerActivity)))
            .placeholder(R.drawable.player_placeholder)
            .into(albumImage)
    }

    private fun updateLikeButtonIcon(track: Track) {
        if (track.isFavorite) {
            likeButton.setImageResource(R.drawable.like_active)
        } else {
            likeButton.setImageResource(R.drawable.like_inactive)
        }
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

    private fun updatePlaylists(newPlaylists: List<Playlist>) {
        playlists.clear()
        playlists.addAll(newPlaylists)
        playlistAdapter.notifyDataSetChanged()
    }

    companion object {
        private const val INTENT_TRACK_KEY = "TRACK"
        private const val START_TIME = "00:00"
    }
}