package com.example.playlistmaker.search.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.Scaffold
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.core.model.Track
import com.example.playlistmaker.search.ui.compose.SearchRoute
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.ui.core.TopBar
import com.example.playlistmaker.ui.theme.MyPlaylistMakerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {
    private val searchViewModel: SearchViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                MyPlaylistMakerTheme {
                    Scaffold(
                        topBar = { TopBar(getString(R.string.search)) }
                    ) { inner ->
                        SearchRoute(
                            vm = searchViewModel,
                            onTrackClick = { goToPlayerActivity(it) }, // переход в плеер
                            contentPadding = inner
                        )
                    }
                }
            }
        }
    }

    private fun goToPlayerActivity(track: Track) {
        val bundle = Bundle().apply {
            putSerializable(INTENT_TRACK_KEY, track)
        }

        findNavController().navigate(R.id.action_searchFragment_to_playerActivity, bundle)
    }

    companion object {
        private const val SEARCH_TEXT_KEY = "SEARCH_TEXT"
        private const val INTENT_TRACK_KEY = "TRACK"
    }
}