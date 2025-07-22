package com.example.playlistmaker.playlist.ui.fragment

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.playlistmaker.R
import com.example.playlistmaker.core.model.Playlist
import com.example.playlistmaker.core.model.Track
import com.example.playlistmaker.core.util.Result
import com.example.playlistmaker.databinding.FragmentPlaylistBinding
import com.example.playlistmaker.playlist.ui.viewmodel.PlaylistViewModel
import com.example.playlistmaker.search.ui.adapter.TracksAdapter
import com.example.playlistmaker.utils.calculateTotalDurationInMinutes
import com.example.playlistmaker.utils.dpToPx
import com.example.playlistmaker.utils.throttle
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File

class PlaylistFragment : Fragment() {
    private lateinit var binding: FragmentPlaylistBinding
    private val playlistViewModel: PlaylistViewModel by viewModel()

    private var playlistId: Int? = null

    private lateinit var arrowBackButton: ImageButton
    private lateinit var playlistImage: ImageView
    private lateinit var playlistPlaceholder: ImageView
    private lateinit var playlistName: TextView
    private lateinit var playlistDescription: TextView
    private lateinit var playlistTimeAndCount: TextView
    private lateinit var shareButton: ImageView
    private lateinit var moreButton: ImageView

    private lateinit var bottomSheetTracks: LinearLayout
    private lateinit var bottomSheetTracksBehavior: BottomSheetBehavior<LinearLayout>
    private lateinit var bottomSheetRecycleView: RecyclerView

    private lateinit var tracksAdapter: TracksAdapter
    private val playlistTracks: MutableList<Track> = mutableListOf()
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private lateinit var bottomSheetMoreButton: ConstraintLayout
    private lateinit var bottomSheetMoreButtonBehavior: BottomSheetBehavior<ConstraintLayout>
    private lateinit var bottomSheetPlaylistImage: ImageView
    private lateinit var bottomSheetPlaylistName: TextView
    private lateinit var bottomSheetPlaylistTrackCount: TextView
    private lateinit var bottomSheetShareButton: View
    private lateinit var bottomSheetEditButton: View
    private lateinit var bottomSheetDeleteButton: View

    private lateinit var overlay: View

    lateinit var deletePlaylistTrackDialog: MaterialAlertDialogBuilder
    lateinit var deletePlaylistDialog: MaterialAlertDialogBuilder

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPlaylistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        playlistId = arguments?.getInt(ID)

        arrowBackButton = binding.arrowBackPlaylist
        playlistImage = binding.playlistImage
        playlistPlaceholder = binding.playlistPlaceholder
        playlistName = binding.playlistName
        playlistDescription = binding.playlistDescription
        playlistTimeAndCount = binding.playlistTimeAndCount
        shareButton = binding.shareButton
        moreButton = binding.moreButton
        bottomSheetRecycleView = binding.bottomSheetTracksRecycleView
        bottomSheetPlaylistImage = binding.bottomSheetPlaylistImage
        bottomSheetPlaylistName = binding.bottomSheetPlaylistName
        bottomSheetPlaylistTrackCount = binding.bottomSheetTracksCount
        bottomSheetShareButton = binding.bottomSheetShareButton
        bottomSheetEditButton = binding.bottomSheetUpdatePlaylistButton
        bottomSheetDeleteButton = binding.bottomSheetDeletePlaylistButton

        bottomSheetTracks = binding.bottomSheetTracksContainer
        bottomSheetMoreButton = binding.bottomSheetMoreContainer

        overlay = binding.overlay

        onTrackClickDebounce = throttle(
            CLICK_DEBOUNCE_DELAY,
            viewLifecycleOwner.lifecycleScope,
            false
        ) { track ->
            goToPlayerActivity(track)
        }

        tracksAdapter = TracksAdapter(
            playlistTracks,
            onTrackClick = { track ->
                onTrackClickDebounce(track)
            },
            onTrackLongClick = { track ->
                buildDeleteTrackDialog(track)
            }
        )

        bottomSheetRecycleView.adapter = tracksAdapter

        bottomSheetTracksBehavior = BottomSheetBehavior.from(bottomSheetTracks)

        bottomSheetMoreButtonBehavior = BottomSheetBehavior.from(bottomSheetMoreButton).apply {
            state = BottomSheetBehavior.STATE_HIDDEN
        }

        playlistViewModel.getPlaylistLiveData().observe(viewLifecycleOwner) { result ->
            updatePlaylistTracks(result.tracks)
            bindPlaylistView(result.playlist)
            bottomSheetTracks.post {
                bottomSheetTracksBehavior.apply {
                    peekHeight = calculateBottomSheetTracksPeekHeight()
                    state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
        }

        playlistViewModel.getPlaylistDeletedLiveData().observe(viewLifecycleOwner) { result ->
            when (result){
                is Result.Success -> findNavController().popBackStack()
                is Result.Failure -> Log.d("PlaylistFragment", "Плейлист не удалён")
            }
        }

        playlistViewModel.getPlaylistById(playlistId!!)

        arrowBackButton.setOnClickListener {
            findNavController().popBackStack()
        }

        shareButton.setOnClickListener {
            buildShare()
        }

        moreButton.setOnClickListener {
            bottomSheetMoreButton.post {
                bottomSheetMoreButtonBehavior.apply {
                    peekHeight = calculateBottomSheetMorePeekHeight()
                    state = BottomSheetBehavior.STATE_COLLAPSED
                }
            }
            bindBottomSheetPlaylistView()
        }

        bottomSheetShareButton.setOnClickListener {
            bottomSheetMoreButtonBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            buildShare()
        }

        bottomSheetEditButton.setOnClickListener {
            val bundle = Bundle().apply {
                putInt(ID, playlistId!!)
                putCharSequence(PLAYLIST_NAME, playlistName.text)
                putCharSequence(PLAYLIST_DESCRIPTION, playlistDescription.text)
            }
            findNavController().navigate(R.id.action_playlistFragment_to_editPlaylistFragment, bundle)
        }

        bottomSheetDeleteButton.setOnClickListener {
            bottomSheetMoreButtonBehavior.state = BottomSheetBehavior.STATE_HIDDEN
            buildDeletePlaylistDialog()
        }

        bottomSheetMoreButtonBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
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

    }

    private fun bindPlaylistView(playlist: Playlist) {
        playlistName.text = playlist.playlistName

        if (playlist.playlistDescription.isEmpty()) {
            playlistDescription.isVisible = false
        } else {
            playlistDescription.text = playlist.playlistDescription
            playlistDescription.isVisible = true
        }

        val tracksCount = playlist.trackCount
        val totalTracksTime = calculateTotalDurationInMinutes(playlistTracks)

        playlistTimeAndCount.text = getString(R.string.playlist_time_and_count, totalTracksTime, tracksCount)

        val filePath = File(requireContext()
            .getExternalFilesDir(Environment.DIRECTORY_PICTURES), "playlist-album")
        val file = File(filePath, "${playlist.playlistName}.jpg")
        Glide.with(playlistImage)
            .load(file)
            .transform(
                CenterCrop()
            )
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    playlistPlaceholder.isVisible = true
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    playlistPlaceholder.isVisible = false
                    return false
                }
            })
            .into(playlistImage)
    }

    private fun bindBottomSheetPlaylistView() {
        val drawable = playlistImage.drawable

        Glide.with(bottomSheetPlaylistImage)
            .load(drawable)
            .transform(
                CenterCrop(),
                RoundedCorners(dpToPx(8f, requireContext())))
            .placeholder(R.drawable.placeholder)
            .into(bottomSheetPlaylistImage)
        bottomSheetPlaylistName.text = playlistName.text
        bottomSheetPlaylistTrackCount.text = getString(R.string.tracks_count, playlistTracks.size)
    }

    private fun updatePlaylistTracks(tracks: List<Track>) {
        playlistTracks.clear()
        playlistTracks.addAll(tracks)
        tracksAdapter.notifyDataSetChanged()

        if (playlistTracks.isEmpty()) {
            Toast
                .makeText(
                    requireContext(),
                    getString(R.string.no_tracks_in_playlist),
                    Toast.LENGTH_SHORT
                )
                .show()
        }
    }

    private fun calculateBottomSheetTracksPeekHeight(): Int {
        val screenHeight = binding.root.height
        val iconBottom = shareButton.bottom

        return screenHeight - iconBottom - dpToPx(24f, requireContext())
    }

    private fun calculateBottomSheetMorePeekHeight(): Int {
        val screenHeight = binding.root.height
        val iconBottom = playlistName.bottom

        return screenHeight - iconBottom - dpToPx(8f, requireContext())
    }

    private fun goToPlayerActivity(track: Track) {
        val bundle = Bundle().apply {
            putSerializable(INTENT_TRACK_KEY, track)
        }

        findNavController().navigate(R.id.action_playlistFragment_to_playerActivity, bundle)
    }

    private fun buildDeleteTrackDialog(track: Track) {
        deletePlaylistTrackDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_track_dialog_title))
            .setMessage("")
            .setNegativeButton(getString(R.string.no)) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                playlistViewModel.deleteTrackFromPlaylist(playlistId!!, track.trackId)
            }

        deletePlaylistTrackDialog.show()
    }

    private fun buildDeletePlaylistDialog( ) {
        deletePlaylistDialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(R.string.delete_playlist_dialog_title, playlistName.text))
            .setMessage("")
            .setNegativeButton(getString(R.string.no)) { dialog, which ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.yes)) { dialog, which ->
                playlistViewModel.deletePlaylist(playlistId!!)
            }

        deletePlaylistDialog.show()
    }

    private fun buildShare() {
        if (playlistTracks.isEmpty()) {
            Toast.makeText(
                requireContext(),
                getString(R.string.playlist_tracks_is_empty),
                Toast.LENGTH_LONG).show()
        } else {
            val shareAppIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, buildPlaylistShareText())
            }

            requireContext().startActivity(shareAppIntent)
        }
    }

    private fun buildPlaylistShareText(): String {
        val playlistName = playlistName.text
        val playlistDescription = playlistDescription.text
        val trackCount = playlistTracks.size

        val shareText = StringBuilder()
        shareText.appendLine(playlistName)
        if (playlistDescription.isNotBlank()) shareText.appendLine(playlistDescription)

        shareText.appendLine(getString(R.string.tracks_count, trackCount))

        playlistTracks.forEachIndexed { index, track ->
            val buildString = "${index + 1}. ${track.artistName} - ${track.trackName} (${track.trackTime})"
            shareText.appendLine(buildString)
        }

        return shareText.trimEnd().toString()
    }

    companion object {
        private const val ID = "PLAYLIST_ID"
        private const val PLAYLIST_NAME = "PLAYLIST_NAME"
        private const val PLAYLIST_DESCRIPTION = "PLAYLIST_DESCRIPTION"
        private const val INTENT_TRACK_KEY = "TRACK"
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }
}