package com.example.playlistmaker.search.ui.compose

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.core.model.Track
import com.example.playlistmaker.search.ui.compose.preview.previewTacks10
import com.example.playlistmaker.search.ui.compose.preview.previewTracks5
import com.example.playlistmaker.search.ui.viewmodel.SearchTracksState.Error
import com.example.playlistmaker.search.ui.viewmodel.SearchTracksState.Idle
import com.example.playlistmaker.search.ui.viewmodel.SearchTracksState.Loading
import com.example.playlistmaker.search.ui.viewmodel.SearchTracksState.Success
import com.example.playlistmaker.search.ui.viewmodel.SearchUiState
import com.example.playlistmaker.search.ui.viewmodel.SearchViewModel
import com.example.playlistmaker.ui.core.TopBar
import com.example.playlistmaker.ui.core.TrackItem
import com.example.playlistmaker.ui.theme.Blue
import com.example.playlistmaker.ui.theme.DarkNavy
import com.example.playlistmaker.ui.theme.MyPlaylistMakerTheme
import com.example.playlistmaker.utils.debounce
import com.example.playlistmaker.utils.throttle

private const val SEARCH_DEBOUNCE_DELAY = 2000L
private const val CLICK_DEBOUNCE_DELAY = 1000L

@Composable
fun SearchRoute(
    vm: SearchViewModel,
    onTrackClick: (Track) -> Unit,
    contentPadding: PaddingValues,
) {
    val tracksState by vm.getTracksStateLiveData().observeAsState(Idle)
    val historyTracks by vm.getHistoryTracksLiveData().observeAsState(emptyList())

    val state = SearchUiState(
        tracksState = tracksState,
        historyTracks = historyTracks
    )

    SearchScreen(
        state = state,
        onTrackClick = onTrackClick,
        onClearSearchTracksState = { vm.clearSearchTracksState() },
        onSearchTracks = { query -> vm.searchTracks(query) },
        onSaveTrackInHistoryTrackList = { track -> vm.saveTrackInHistoryTrackList(track) },
        onSaveSearchHistoryInPreferences = { vm.saveSearchHistoryInPreferences() },
        onDeleteSearchHistory = { vm.deleteSearchHistory() },
        contentPadding
    )
}

@Composable
fun SearchScreen(
    state: SearchUiState,
    onTrackClick: (Track) -> Unit,
    onClearSearchTracksState: () -> Unit,
    onSearchTracks: (String) -> Unit,
    onSaveTrackInHistoryTrackList: (Track) -> Unit,
    onSaveSearchHistoryInPreferences: () -> Unit,
    onDeleteSearchHistory: () -> Unit,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    SaveSearchHistoryOnDispose {
        onSaveSearchHistoryInPreferences()
    }

    var query by rememberSaveable { mutableStateOf("") }
    var isFocused by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val onSearchClickDebounce: (String) -> Unit = remember {
        debounce(SEARCH_DEBOUNCE_DELAY, scope, true) { query ->
            if (query.isNotEmpty()) {
                onSearchTracks(query)
            }
        }
    }
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
            .padding(contentPadding)
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.TopCenter
    ) {
        SearchField(
            query = query,
            onQueryChange = { newQuery ->
                query = newQuery
                onSearchClickDebounce(newQuery)
            },
            isFocused = isFocused,
            onFocusChange = { isFocused = it },
            onSearchTracks = onSearchTracks,
            onClearSearchTracksState = onClearSearchTracksState
        )

        val tracksState = state.tracksState
        when (tracksState) {
            is Idle -> { }
            is Loading -> { LoadingIndicator() }
            is Success -> {
                val tracks = tracksState.tracks
                if (tracks.isNotEmpty()) {
                    TracksList(
                        tracks = tracks,
                        onTrackClick = onTrackClickThrottle,
                        onSaveTrackInHistoryTrackList = onSaveTrackInHistoryTrackList
                    )
                } else {
                    SearchEmptyPlaceholder()
                }
            }
            is Error -> NetworkErrorPlaceholder(
                onRetry = { onSearchTracks(query) }
            )
        }

        if (isFocused &&
            query.isEmpty() &&
            state.historyTracks.isNotEmpty() &&
            state.tracksState is Idle
        ) {
            HistoryList(
                history = state.historyTracks,
                onTrackClick = onTrackClickThrottle,
                onDeleteSearchHistory = onDeleteSearchHistory
            )
        }
    }
}

@Composable
fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    isFocused: Boolean,
    onFocusChange: (Boolean) -> Unit,
    onSearchTracks: (String) -> Unit,
    onClearSearchTracksState: () -> Unit
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        singleLine = true,
        maxLines = 1,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, top = 8.dp, end = 16.dp)
            .height(36.dp)
            .onFocusChanged { focusState ->
                onFocusChange(focusState.isFocused)
            },
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = DarkNavy),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(
            onDone = {
                onSearchTracks(query)
            }
        ),
        cursorBrush = SolidColor(Blue),
        decorationBox = { innerTextField ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.surface,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .fillMaxSize()
            ) {
                // Левая иконка
                Icon(
                    painter = painterResource(R.drawable.search_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(start = 16.dp)
                        .size(16.dp),
                    tint = MaterialTheme.colorScheme.onSurface
                )

                Spacer(Modifier.width(8.dp))

                // Текст + placeholder
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp, end = 8.dp)
                    ,
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (query.isEmpty()) {
                        Text(
                            text = stringResource(R.string.search),
                            color = MaterialTheme.colorScheme.onSurface,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    innerTextField()
                }

                // Правая иконка
                if (query.isNotEmpty()) {
                    IconButton(onClick = {
                        onQueryChange("")
                        onClearSearchTracksState()
                        keyboardController?.hide()
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.button_clear),
                            contentDescription = null,
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    )
}

@Composable
fun TracksList(
    tracks: List<Track>,
    onTrackClick: (Track) -> Unit,
    onSaveTrackInHistoryTrackList: (Track) -> Unit
) {

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 68.dp)
    ) {
        items(items = tracks) {
            TrackItem(
                track = it,
                onClick = {
                    onSaveTrackInHistoryTrackList(it)
                    onTrackClick(it)
                }
            )
        }
    }
}

@Composable
fun LoadingIndicator() {
    CircularProgressIndicator(
        modifier = Modifier
            .size(44.dp)
            .offset(y = 184.dp),
        color = Blue
    )
}


@Composable
fun SaveSearchHistoryOnDispose(
    onSaveSearchHistoryInPreferences: () -> Unit,
) {
    DisposableEffect(Unit) {
        onDispose {
            onSaveSearchHistoryInPreferences()
        }
    }
}

@Composable
fun SearchEmptyPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, top = 154.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.search_not_found),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )

        Text(
            text = stringResource(R.string.no_tracks_found),
            modifier = Modifier.padding(top = 16.dp),
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.titleMedium
        )
    }
}

@Composable
fun NetworkErrorPlaceholder(
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, top = 154.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.search_internet_problems),
            contentDescription = null,
            modifier = Modifier.size(120.dp)
        )

        Text(
            text = stringResource(R.string.problems_when_searching_for_a_tracks),
            modifier = Modifier.padding(top = 16.dp),
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )

        Button(
            onClick = onRetry,
            modifier = Modifier
                .padding(top = 24.dp)
                .width(91.dp)
                .height(36.dp)
            ,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,    // фон кнопки
                contentColor = MaterialTheme.colorScheme.onTertiary
            ),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            Text(
                text = stringResource(R.string.update_search_activity),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

@Composable
fun HistoryList(
    history: List<Track>,
    onTrackClick: (Track) -> Unit,
    onDeleteSearchHistory: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(top = 76.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.search_history),
            modifier = Modifier
                .height(52.dp)
                .wrapContentHeight(Alignment.CenterVertically)
            ,
            color = MaterialTheme.colorScheme.onPrimary,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleMedium
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(items = history) {
                TrackItem(
                    track = it,
                    onClick = { onTrackClick(it) }
                )
            }
        }
        Button(
            onClick = onDeleteSearchHistory,
            modifier = Modifier
                .padding(top = 24.dp)
                .width(148.dp)
                .height(36.dp)
            ,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = MaterialTheme.colorScheme.onTertiary
            ),
            contentPadding = PaddingValues(horizontal = 4.dp)
        ) {
            Text(
                text = stringResource(R.string.clear_search_history),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleSmall
            )
        }
    }
}

@Preview(
    name = "Light with TextField",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun SearchScreenLightWithTextFieldPreview() {
    MyPlaylistMakerTheme(darkTheme = false) {
        Scaffold(
            topBar = { TopBar(name = "Поиск") }
        ) { innerPadding ->
            SearchScreen(
                state = SearchUiState(
                    tracksState = Idle,
                    historyTracks = emptyList()
                ),
                onSearchTracks = { },
                onTrackClick = { },
                onClearSearchTracksState = { },
                onSaveTrackInHistoryTrackList = { },
                onSaveSearchHistoryInPreferences = { },
                onDeleteSearchHistory = { },
                contentPadding = innerPadding
            )
        }
    }
}

@Preview(
    name = "Night with TextField",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun SearchScreenDarkWithTextFieldPreview() {
    MyPlaylistMakerTheme(darkTheme = true) {
        Scaffold(
            topBar = { TopBar(name = "Поиск") }
        ) { innerPadding ->
            SearchScreen(
                state = SearchUiState(
                    tracksState = Idle,
                    historyTracks = emptyList()
                ),
                onTrackClick = { },
                onClearSearchTracksState = { },
                onSearchTracks = { },
                onSaveTrackInHistoryTrackList = { },
                onSaveSearchHistoryInPreferences = { },
                onDeleteSearchHistory = { },
                contentPadding = innerPadding
            )
        }
    }
}

@Preview(
    name = "Light with TracksList",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun SearchScreenLightWithTracksListPreview() {
    MyPlaylistMakerTheme(darkTheme = false) {
        Scaffold(
            topBar = { TopBar(name = "Поиск") }
        ) { innerPadding ->
            SearchScreen(
                state = SearchUiState(
                    tracksState = Success(previewTacks10),
                    historyTracks = emptyList()
                ),
                onTrackClick = { },
                onClearSearchTracksState = { },
                onSearchTracks = { },
                onSaveTrackInHistoryTrackList = { },
                onSaveSearchHistoryInPreferences = { },
                onDeleteSearchHistory = { },
                contentPadding = innerPadding
            )
        }
    }
}

@Preview(
    name = "Dark with TracksList",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun SearchScreenDarkWithTracksListPreview() {
    MyPlaylistMakerTheme(darkTheme = false) {
        Scaffold(
            topBar = { TopBar(name = "Поиск") }
        ) { innerPadding ->
            SearchScreen(
                state = SearchUiState(
                    tracksState = Success(previewTacks10),
                    historyTracks = emptyList()
                ),
                onTrackClick = { },
                onClearSearchTracksState = { },
                onSearchTracks = { },
                onSaveTrackInHistoryTrackList = { },
                onSaveSearchHistoryInPreferences = { },
                onDeleteSearchHistory = { },
                contentPadding = innerPadding
            )
        }
    }
}

@Preview(
    name = "Light with HistoryList",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun SearchScreenLightWithHistoryListPreview() {
    MyPlaylistMakerTheme(darkTheme = false) {
        Scaffold(
            topBar = { TopBar(name = "Поиск") }
        ) { innerPadding ->
            SearchScreen(
                state = SearchUiState(
                    tracksState = Idle,
                    historyTracks = previewTracks5
                ),
                onTrackClick = { },
                onSearchTracks = { },
                onClearSearchTracksState = { },
                onSaveTrackInHistoryTrackList = { },
                onSaveSearchHistoryInPreferences = { },
                onDeleteSearchHistory = { },
                contentPadding = innerPadding
            )
        }
    }
}

@Preview(
    name = "Dark with HistoryList",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun SearchScreenDarkWithHistoryListPreview() {
    MyPlaylistMakerTheme(darkTheme = true) {
        Scaffold(
            topBar = { TopBar(name = "Поиск") }
        ) { innerPadding ->
            SearchScreen(
                state = SearchUiState(
                    tracksState = Idle,
                    historyTracks = previewTracks5
                ),
                onTrackClick = { },
                onSearchTracks = { },
                onClearSearchTracksState = { },
                onSaveTrackInHistoryTrackList = { },
                onSaveSearchHistoryInPreferences = { },
                onDeleteSearchHistory = { },
                contentPadding = innerPadding
            )
        }
    }
}

@Preview(
    name = "Light with search empty",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun SearchScreenLightWithSearchEmptyPreview() {
    MyPlaylistMakerTheme(darkTheme = false) {
        Scaffold(
            topBar = { TopBar(name = "Поиск") }
        ) { innerPadding ->
            SearchScreen(
                state = SearchUiState(
                    tracksState = Success(emptyList()),
                    historyTracks = emptyList()
                ),
                onTrackClick = { },
                onClearSearchTracksState = { },
                onSearchTracks = { },
                onSaveTrackInHistoryTrackList = { },
                onSaveSearchHistoryInPreferences = { },
                onDeleteSearchHistory = { },
                contentPadding = innerPadding
            )
        }
    }
}

@Preview(
    name = "Dark with search empty",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun SearchScreenDarkWithSearchEmptyPreview() {
    MyPlaylistMakerTheme(darkTheme = true) {
        Scaffold(
            topBar = { TopBar(name = "Поиск") }
        ) { innerPadding ->
            SearchScreen(
                state = SearchUiState(
                    tracksState = Success(emptyList()),
                    historyTracks = emptyList()
                ),
                onTrackClick = { },
                onSearchTracks = { },
                onClearSearchTracksState = { },
                onSaveTrackInHistoryTrackList = { },
                onSaveSearchHistoryInPreferences = { },
                onDeleteSearchHistory = { },
                contentPadding = innerPadding
            )
        }
    }
}

@Preview(
    name = "Light network error",
    showSystemUi = true,
    showBackground = true
)
@Composable
fun NetworkErrorLightPreview() {
    MyPlaylistMakerTheme(
        darkTheme = false
    ) {
        Scaffold(
            topBar = { TopBar(name = "Поиск") }
        ) { innerPadding ->
            SearchScreen(
                state = SearchUiState(
                    tracksState = Error,
                    historyTracks = emptyList()
                ),
                onTrackClick = { },
                onClearSearchTracksState = { },
                onSearchTracks = { },
                onSaveTrackInHistoryTrackList = { },
                onSaveSearchHistoryInPreferences = { },
                onDeleteSearchHistory = { },
                contentPadding = innerPadding
            )
        }
    }
}

@Preview(
    name = "Dark network error",
    showSystemUi = true,
    showBackground = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
fun NetworkErrorDarkPreview() {
    MyPlaylistMakerTheme(
        darkTheme = true
    ) {
        Scaffold(
            topBar = { TopBar(name = "Поиск") }
        ) { innerPadding ->
            SearchScreen(
                state = SearchUiState(
                    tracksState = Error,
                    historyTracks = emptyList()
                ),
                onTrackClick = { },
                onClearSearchTracksState = { },
                onSearchTracks = { },
                onSaveTrackInHistoryTrackList = { },
                onSaveSearchHistoryInPreferences = { },
                onDeleteSearchHistory = { },
                contentPadding = innerPadding
            )
        }
    }
}

