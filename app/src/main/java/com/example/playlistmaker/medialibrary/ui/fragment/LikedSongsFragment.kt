package com.example.playlistmaker.medialibrary.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.core.model.Track
import com.example.playlistmaker.medialibrary.ui.compose.LikesSongsRoute
import com.example.playlistmaker.medialibrary.ui.viewmodel.LikedSongsViewModel
import com.example.playlistmaker.ui.theme.MyPlaylistMakerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class LikedSongsFragment : Fragment() {
    private val likedSongsViewModel: LikedSongsViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                MyPlaylistMakerTheme {
                    LikesSongsRoute(
                        vm = likedSongsViewModel,
                        onTrackClick = { goToPlayerActivity(it) }
                    )

                }
            }
        }
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

        fun newInstance(): Fragment {
            return LikedSongsFragment()
        }
    }
}