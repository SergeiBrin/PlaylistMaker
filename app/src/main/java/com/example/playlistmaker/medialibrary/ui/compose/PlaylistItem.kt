package com.example.playlistmaker.medialibrary.ui.compose

import android.os.Environment
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.playlistmaker.R
import com.example.playlistmaker.core.model.Playlist
import com.example.playlistmaker.ui.theme.MyPlaylistMakerTheme
import java.io.File

@Composable
fun PlaylistItem(
    playlist: Playlist,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
       modifier = modifier
           .fillMaxWidth()
           .clickable {
               onClick()
           }
           .background(MaterialTheme.colorScheme.background)
    ) {
        PlaylistImage(playlist.playlistName)
        Spacer(Modifier.height(4.dp))
        Text(
            text = playlist.playlistName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = stringResource(R.string.tracks_count, playlist.trackCount),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun PlaylistImage(
    playlistName: String
) {
    val inPreview: Boolean = LocalInspectionMode.current
    val context = LocalContext.current
    val filePath = remember(context) {
        File(
            context.getExternalFilesDir(Environment.DIRECTORY_PICTURES),
            "playlist-album"
        )
    }

    val file = remember(filePath, playlistName) {
        File(filePath, "$playlistName.jpg")
    }

    val key = "${file.absolutePath}:${file.lastModified()}:${file.length()}"

    val request = ImageRequest.Builder(context)
        .data(file)
        .diskCacheKey(key)
        .memoryCacheKey(key)
        .build()

    Box(
        modifier = Modifier.aspectRatio(1f).fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(R.drawable.playlists_placeholder),
            contentDescription = null,
            alignment = Alignment.Center
        )

        if (!inPreview) {
            AsyncImage(
                model = request,
                contentDescription = null,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
        } else {
            Image(
                painter = painterResource(R.drawable.preview_image),
                contentDescription = null,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
        }
    }
}

@Preview(
    name = "Light",
    showBackground = true
)
@Composable
fun PlaylistItemLightPreview() {
    MyPlaylistMakerTheme(darkTheme = false) {
        PlaylistItem(
            Playlist(
                id = 1,
                playlistName = "Summer party",
                playlistDescription = "",
                playlistImageUri = null,
                trackCount = 25
            ),
            onClick = { }
        )
    }
}

@Preview(
    name = "Dark",
    showBackground = true
)
@Composable
fun PlaylistItemDarkPreview() {
    MyPlaylistMakerTheme(darkTheme = true) {
        PlaylistItem(
            Playlist(
                id = 1,
                playlistName = "Summer party",
                playlistDescription = "",
                playlistImageUri = null,
                trackCount = 25
            ),
            onClick = { }
        )
    }
}