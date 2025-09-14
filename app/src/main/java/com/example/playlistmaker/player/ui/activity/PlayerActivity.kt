package com.example.playlistmaker.player.ui.activity

import android.Manifest
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.playlistmaker.R
import com.example.playlistmaker.core.model.Playlist
import com.example.playlistmaker.core.model.Track
import com.example.playlistmaker.player.ui.adapter.PlayerPlaylistAdapter
import com.example.playlistmaker.player.ui.customview.PlaybackButtonView
import com.example.playlistmaker.player.ui.result.AddTrackInPlaylistResult
import com.example.playlistmaker.player.ui.result.PlayerState
import com.example.playlistmaker.player.ui.result.PlayerUiState
import com.example.playlistmaker.player.ui.service.MusicService
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

    private lateinit var playbackButton: PlaybackButtonView
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

    private var mBound: Boolean = false

    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(
            p0: ComponentName?,
            p1: IBinder?
        ) {
            val binder = p1 as MusicService.MusicServiceBinder
            playerViewModel.setAudioPlayerControl(binder.getService())
            mBound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            playerViewModel.removeAudioPlayerControl()
            mBound = false
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            bindMusicService()
         } else {
            Toast.makeText(this, "Can't bind service!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_player)

        track = intent.getSerializableExtra(INTENT_TRACK_KEY) as Track

        arrowBackButton = findViewById(R.id.arrow_back_player)
        albumImage = findViewById(R.id.album_image)
        trackName = findViewById(R.id.track_name)
        artistName = findViewById(R.id.artist_name)
        addButton = findViewById(R.id.add_playlist_button)
        likeButton = findViewById(R.id.like_button)
        playbackTime = findViewById(R.id.playback_time)
        duration = findViewById(R.id.duration_value)
        albumName = findViewById(R.id.album_value)
        albumYear = findViewById(R.id.year_value)
        genre = findViewById(R.id.genre_value)
        country = findViewById(R.id.country_value)

        playbackButton = findViewById(R.id.playback_button_view)

        bottomSheet = findViewById(R.id.bottom_sheet_tracks_container)
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        createPlaylist = findViewById(R.id.create_playlist_button)
        playlistRecycleView = findViewById(R.id.bottom_sheet_tracks_recycle_view)
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
                    Toast.makeText(
                        this,
                        "${getString(R.string.add_track_in_playlist_success)} ${result.playlistMame}",
                        Toast.LENGTH_LONG)
                        .show()
                }
                is AddTrackInPlaylistResult.Failure ->
                    Toast.makeText(
                        this,
                        "${getString(R.string.add_track_in_playlist_failure)} ${result.playlistMame}",
                        Toast.LENGTH_LONG)
                        .show()
            }

        }

        playerViewModel.getPlayerStateLiveData().observe(this) { playerState ->
            when (playerState) {
                is PlayerState.StateDefault,
                is PlayerState.StatePlaying,
                is PlayerState.StatePaused -> {
                    updatePlaybackButtonView(playerState)
                    updateCurrentTrackTime(playerState.progress)
                }
                is PlayerState.StatePrepared -> {
                    prepareViewForPlayback()
                    updatePlaybackButtonView(playerState)
                    updateCurrentTrackTime(playerState.progress)
                }
            }
        }

        playerViewModel.getPlayerUiStateLiveData().observe(this) { playerUiState ->
            when (playerUiState) {
                is PlayerUiState.TrackData -> updateLikeButtonIcon(playerUiState.track)
                // is PlayerUiState.CurrentTrackTimeLiveData -> updateCurrentTrackTime(playerUiState.trackTime)
                PlayerUiState.FavoriteTrackSuccessEvent -> {
                    track.isFavorite = true
                    likeButton.setImageResource(R.drawable.like_active)
                }
                PlayerUiState.FavoriteTrackFailureEvent -> {
                    track.isFavorite = false
                    likeButton.setImageResource(R.drawable.like_inactive)
                }
            }
        }

        playerViewModel.getPlaylistsLiveData().observe(this) {
            updatePlaylists(it)
        }

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

        playbackButton.setOnClickListener {
            playerViewModel.playbackControl()
        }

        likeButton.setOnClickListener {
            playerViewModel.onFavoriteClicked(track)
        }

        populatePlayerActivityViews()
    }

    override fun onStart() {
        super.onStart()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        } else {
            bindMusicService()
        }
    }

    override fun onResume() {
        super.onResume()
        playerViewModel.stopForeground()
    }

    override fun onPause() {
        super.onPause()
        if (!isFinishing) playerViewModel.startForeground()
    }

    override fun onDestroy() {
        super.onDestroy()
        unbindMusicService()
    }

    private fun populatePlayerActivityViews() {
        trackName.text = track.trackName
        artistName.text = track.artistName
        playbackTime.text =  START_TIME
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

    private fun updatePlaybackButtonView(playerState: PlayerState) {
        playbackButton.updateBitmap(playerState)
    }

    private fun prepareViewForPlayback() {
        playbackButton.isEnabled = true
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

    private fun bindMusicService() {
        if (!mBound) {
            val intent = Intent(
                this@PlayerActivity,
                MusicService::class.java
            ).apply {
                putExtra(INTENT_TRACK_URL, track.previewUrl)
                putExtra(INTENT_TRACK_NAME, track.trackName)
                putExtra(INTENT_ARTIST_NAME, track.artistName)
            }

            bindService(intent, serviceConnection, BIND_AUTO_CREATE)
        }
    }

    private fun unbindMusicService() {
        unbindService(serviceConnection)
    }

    companion object {
        private const val INTENT_TRACK_KEY = "TRACK"
        private const val INTENT_TRACK_URL = "TRACK_URL"
        private const val INTENT_TRACK_NAME = "TRACK_NAME"
        private const val INTENT_ARTIST_NAME = "ARTIST_NAME"
        private const val START_TIME = "00:00"
    }
}