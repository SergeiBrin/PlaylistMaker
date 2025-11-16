package com.example.playlistmaker.ui.core

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.playlistmaker.ui.theme.MyPlaylistMakerTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    name: String? = null,
    onBack: (() -> Unit)? = null
) {
    TopAppBar(
        title = {
            name?.let {
                Text(
                    text = it,
                    style = MaterialTheme.typography.titleLarge
                    )
            }
        },
        navigationIcon = {
            onBack?.let {
                IconButton(onClick = it) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Назад"
                    )
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            titleContentColor = MaterialTheme.colorScheme.onPrimary,
            navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
        )
    )
}

@Preview(
    name = "Light theme - text",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun TopBarLightPreview() {
    MyPlaylistMakerTheme(darkTheme = false) {
        TopBar("Поиск")
    }
}

@Preview(
    name = "Dark theme - text",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun TopBarDarkPreview() {
    MyPlaylistMakerTheme(darkTheme = true) {
        TopBar("Поиск")
    }
}

@Preview(
    name = "Light theme - text and back button",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun TopBarWithBackButtonLightPreview() {
    MyPlaylistMakerTheme(darkTheme = false) {
        TopBar(
            "Поиск",
            {}
        )
    }
}

@Preview(
    name = "Dark theme - text and back button",
    showBackground = true,
    showSystemUi = true
)
@Composable
fun TopBarWithBackButtonDarkPreview() {
    MyPlaylistMakerTheme(darkTheme = true) {
        TopBar(
            "Поиск",
            {}
        )
    }
}
