package com.example.playlistmaker.medialibrary.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.playlistmaker.R
import com.example.playlistmaker.core.model.Playlist
import com.example.playlistmaker.medialibrary.ui.viewmodel.PlaylistsViewModel
import com.example.playlistmaker.utils.throttle

private const val CLICK_DEBOUNCE_DELAY = 1000L

@Composable
fun PlaylistRoute(
    vm: PlaylistsViewModel,
    onPlaylistClick: (Playlist) -> Unit,
    onCreatePlaylistClick: () -> Unit
) {
    val playlists by vm.playlists.collectAsStateWithLifecycle()

    PlaylistScreen(
        playlists = playlists,
        onPlaylistClick = onPlaylistClick,
        onCreatePlaylistClick = onCreatePlaylistClick
    )
}

@Composable
fun PlaylistScreen(
    playlists: List<Playlist>,
    onPlaylistClick: (Playlist) -> Unit,
    onCreatePlaylistClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val onPlaylistClickThrottle: (Playlist) -> Unit =  remember {
        throttle(
            CLICK_DEBOUNCE_DELAY,
            scope,
            false
        ) { playlist ->
            onPlaylistClick(playlist)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        CreatePlaylistButton(
            onCreatePlaylistClick = onCreatePlaylistClick
        )

        if (playlists.isNotEmpty()) {
            PlaylistGrid(
                playlists = playlists,
                onPlaylistClick = onPlaylistClickThrottle
            )
        } else {
            PlaylistEmptyPlaceholder()
        }
    }
}

@Composable
fun PlaylistGrid(
    playlists: List<Playlist>,
    onPlaylistClick: (Playlist) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                top = 76.dp,
                end = 16.dp
            ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(
            items = playlists,
            key = { it.id }
        ) {
            PlaylistItem(
                playlist = it,
                onClick = {
                    onPlaylistClick(it)
                }
            )
        }
    }
}

@Composable
fun CreatePlaylistButton(
    onCreatePlaylistClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onCreatePlaylistClick,
        modifier = modifier
            .padding(top = 24.dp)
            .width(133.dp)
            .height(36.dp)
        ,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.tertiary,
            contentColor = MaterialTheme.colorScheme.onTertiary
        ),
        contentPadding = PaddingValues(horizontal = 8.dp)
    ) {
        Text(
            text = stringResource(R.string.new_playlist),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall
        )
    }
}

@Composable
fun PlaylistEmptyPlaceholder(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = 24.dp, top = 106.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.search_not_found),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )

        Text(
            text = stringResource(R.string.not_found_playlist),
            modifier = Modifier.padding(top = 16.dp),
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleMedium
        )
    }
}
