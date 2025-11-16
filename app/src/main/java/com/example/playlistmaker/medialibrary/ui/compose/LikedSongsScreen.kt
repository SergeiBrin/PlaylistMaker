package com.example.playlistmaker.medialibrary.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.playlistmaker.R
import com.example.playlistmaker.core.model.Track
import com.example.playlistmaker.medialibrary.ui.viewmodel.LikedSongsViewModel
import com.example.playlistmaker.ui.core.TrackItem
import com.example.playlistmaker.utils.throttle

private const val CLICK_DEBOUNCE_DELAY = 1000L

@Composable
fun LikesSongsRoute(
    vm: LikedSongsViewModel,
    onTrackClick: (Track) -> Unit,
) {
    val tracks by vm.favoriteTracks.collectAsStateWithLifecycle()

    LikedSongsScreen(
        tracks = tracks,
        onTrackClick = onTrackClick,
    )
}

@Composable
fun LikedSongsScreen(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val onTrackClickThrottle: (Track) -> Unit =  remember {
        throttle(
            CLICK_DEBOUNCE_DELAY,
            scope,
            false
        ) { track ->
            onTrackClick(track)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {

        if (tracks.isNotEmpty()) {
            LikedSongsList(
                tracks = tracks,
                onTrackClick = onTrackClickThrottle
            )
        } else {
            TracksEmptyPlaceholder()
        }
    }
}

@Composable
fun LikedSongsList(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        items(items = tracks) {
            TrackItem(
                track = it,
                onClick = {
                    onTrackClick(it)
                }
            )
        }
    }
}

@Composable
fun TracksEmptyPlaceholder(modifier: Modifier = Modifier) {
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
            text = stringResource(R.string.the_media_is_empty),
            modifier = Modifier.padding(top = 16.dp),
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleMedium
        )
    }
}