package com.example.playlistmaker.medialibrary.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.playlistmaker.R
import com.example.playlistmaker.core.model.Track
import com.example.playlistmaker.databinding.FragmentLikedSongsBinding
import com.example.playlistmaker.medialibrary.ui.viewmodel.LikedSongsViewModel
import com.example.playlistmaker.search.ui.adapter.TracksAdapter
import com.example.playlistmaker.utils.debounce
import com.example.playlistmaker.utils.throttle
import org.koin.androidx.viewmodel.ext.android.viewModel

class LikedSongsFragment : Fragment() {
    private lateinit var binding: FragmentLikedSongsBinding
    private val likedSongsViewModel: LikedSongsViewModel by viewModel()
    private lateinit var trackAdapter: TracksAdapter
    private lateinit var recycleView: RecyclerView
    private val favoriteTracks: MutableList<Track> = mutableListOf()
    private lateinit var notFoundPlaceholder: LinearLayout
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        binding = FragmentLikedSongsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recycleView = binding.recycleViewFavoriteTracks
        notFoundPlaceholder = binding.notFound

        onTrackClickDebounce = throttle(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
            goToPlayerActivity(track)
        }

        trackAdapter = TracksAdapter(favoriteTracks) { track ->
            onTrackClickDebounce(track)
        }

        recycleView.adapter = trackAdapter

        likedSongsViewModel.getFavoriteTracksOfDb()

        likedSongsViewModel.getFavoriteTracks().observe(viewLifecycleOwner) { tracks ->
            if (tracks.isNotEmpty()) {
                showFavoriteTrackList(tracks)
            } else {
                showNotFoundPlaceholder()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        likedSongsViewModel.getFavoriteTracksOfDb()
    }

    private fun showFavoriteTrackList(tracks: List<Track>) {
        notFoundPlaceholder.isVisible = false
        favoriteTracks.clear()
        favoriteTracks.addAll(tracks)
        trackAdapter.notifyDataSetChanged()
    }

    private fun showNotFoundPlaceholder() {
        favoriteTracks.clear()
        trackAdapter.notifyDataSetChanged()
        notFoundPlaceholder.isVisible = true
    }

    private fun goToPlayerActivity(track: Track) {
        track.isFavorite = true

        val bundle = Bundle().apply {
            putSerializable(INTENT_TRACK_KEY, track)
        }

        findNavController().navigate(R.id.action_media_library_fragment_to_playerActivity, bundle)
    }

    companion object {
        private const val INTENT_TRACK_KEY = "TRACK"
        private const val CLICK_DEBOUNCE_DELAY = 1000L

        fun newInstance(): Fragment {
            return LikedSongsFragment()
        }
    }
}