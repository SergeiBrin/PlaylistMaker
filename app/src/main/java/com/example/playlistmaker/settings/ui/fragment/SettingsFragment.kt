package com.example.playlistmaker.settings.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.material3.Scaffold
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.ui.compose.SettingsRoute
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import com.example.playlistmaker.ui.core.TopBar
import com.example.playlistmaker.ui.theme.MyPlaylistMakerTheme
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsFragment : Fragment() {
    private val settingsViewModel: SettingsViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setContent {
                MyPlaylistMakerTheme {
                    Scaffold(
                        topBar = { TopBar(getString(R.string.settings)) }
                    ) { inner ->
                        SettingsRoute(
                            vm = settingsViewModel,
                            contentPadding = inner
                        )
                    }
                }
            }
        }
    }
}