package com.example.playlistmaker.medialibrary.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.core.model.Playlist
import com.example.playlistmaker.medialibrary.ui.compose.PlaylistRoute
import com.example.playlistmaker.medialibrary.ui.viewmodel.PlaylistsViewModel
import com.example.playlistmaker.ui.theme.MyPlaylistMakerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : Fragment() {
    private val viewModel: PlaylistsViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                MyPlaylistMakerTheme {
                    PlaylistRoute(
                        vm = viewModel,
                        onPlaylistClick = { navigateToPlaylistFragment(it) },
                        onCreatePlaylistClick = { navigateToCreatePlaylistFragment() }
                    )
                }
            }
        }
    }

    private fun navigateToPlaylistFragment(playlist: Playlist) {
        val action = MediaLibraryFragmentDirections
            .actionMediaLibraryFragmentToPlaylistFragment(playlist.id)
        findNavController().navigate(action)
    }

    private fun navigateToCreatePlaylistFragment() {
        findNavController().navigate(R.id.action_mediaLibraryFragment_to_createPlaylistFragment)
    }

    companion object {
        fun newInstance(): Fragment {
            return PlaylistsFragment()
        }
    }
}