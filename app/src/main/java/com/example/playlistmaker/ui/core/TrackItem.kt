package com.example.playlistmaker.ui.core

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.playlistmaker.R
import com.example.playlistmaker.core.model.Track
import com.example.playlistmaker.ui.theme.MyPlaylistMakerTheme

@Composable
fun TrackItem(
    track: Track,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .clickable {
                onClick()
            }
            .padding(horizontal = 12.dp)
            .height(60.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CoverImage(track.artworkUrl100)

        Spacer(Modifier.width(8.dp))

        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = track.trackName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSecondary,
                maxLines = 1,
                textAlign = TextAlign.Start,
                overflow = TextOverflow.Ellipsis
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = track.artistName,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    textAlign = TextAlign.Start,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(Modifier.width(4.dp))

                Text(
                    text = "•",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(Modifier.width(4.dp))

                Text(
                    text = track.trackTime,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }

        Spacer(Modifier.width(16.dp))

        Icon(
            painter = painterResource(R.drawable.arrow_forward_lm),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

    }
}

@Composable
fun CoverImage(
    url: String
) {
    val inPreview: Boolean = LocalInspectionMode.current
    if (!inPreview) {
        AsyncImage(
            model = url,
            contentDescription = null,
            placeholder = painterResource(R.drawable.placeholder),
            error = painterResource(R.drawable.placeholder),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(45.dp)
                .clip(RoundedCornerShape(8.dp))
        )
    } else {
        Image(
           painter = painterResource(R.drawable.preview_image),
           contentDescription = null,
           contentScale = ContentScale.Crop,
           modifier = Modifier
               .size(45.dp)
               .clip(RoundedCornerShape(8.dp))
        )
    }
}

@Preview(
    name = "Light",
    showBackground = true
)
@Composable
fun TrackItemLightPreview() {
    MyPlaylistMakerTheme(darkTheme = false) {
        TrackItem(
            Track(
                1,
                "Yesterday (Remastered)",
                "Scorpions the best",
                "02:35",
                "",
                "Миру мир",
                "15",
                "Лирика",
                "Германия",
                "",
                true
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
fun TrackItemDarkPreview() {
    MyPlaylistMakerTheme(darkTheme = true) {
        TrackItem(
            Track(
                1,
                "Yesterday (Remastered)",
                "Scorpions the best",
                "02:35",
                "",
                "Миру мир",
                "15",
                "Лирика",
                "Германия",
                "",
                true
            ),
            onClick = { }
        )
    }
}