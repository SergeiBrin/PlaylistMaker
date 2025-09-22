package com.example.playlistmaker.settings.ui.compose

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import com.example.playlistmaker.ui.core.TopBar
import com.example.playlistmaker.ui.theme.Blue
import com.example.playlistmaker.ui.theme.BlueLight
import com.example.playlistmaker.ui.theme.GrayLight
import com.example.playlistmaker.ui.theme.GrayNeutral
import com.example.playlistmaker.ui.theme.MyPlaylistMakerTheme

@Composable
fun SettingsRoute(
    vm: SettingsViewModel,
    contentPadding: PaddingValues,
) {
    val context = LocalContext.current
    val checked by vm.getThemeSwitcherLiveData().observeAsState(false)
    
    val onCheckedChange: (Boolean) -> Unit = { isChecked ->
        vm.switchTheme(isChecked)
        vm.saveThemeSettings()
    }

    val onShareApp: () -> Unit = {
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_TEXT,
                context.getString(
                    R.string.uri_android_developer)
            )
        }
        context.startActivity(
            Intent.createChooser(
                intent,
                context.getString(
                    R.string.action_share_app)
            )
        )
    }

    val onSupportContact: () -> Unit = {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:")
            putExtra(
                Intent.EXTRA_EMAIL,
                arrayOf(
                    context.getString(
                        R.string.my_email)
                )
            )
            putExtra(
                Intent.EXTRA_SUBJECT,
                context.getString(
                    R.string.letter_subject)
            )
            putExtra(
                Intent.EXTRA_TEXT,
                context.getString(
                    R.string.letter_text)
            )
        }
        context.startActivity(intent)
    }

    val onOpenUserAgreement: () -> Unit = {
        val uri = Uri.parse(
            context.getString(
                R.string.uri_practicum_offer)
        )
        val intent = Intent(Intent.ACTION_VIEW, uri)
        context.startActivity(intent)
    }

    SettingsScreen(
        checked = checked,
        onCheckedChange = onCheckedChange,
        onShareApp = onShareApp,
        onSupportContact = onSupportContact,
        onOpenUserAgreement = onOpenUserAgreement,
        modifier = Modifier.padding(contentPadding)
    )
}

@Composable
fun SettingsScreen(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onShareApp: () -> Unit,
    onSupportContact: () -> Unit,
    onOpenUserAgreement: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(start = 16.dp, top = 24.dp, end = 16.dp)
            .background(color = MaterialTheme.colorScheme.background)
    ) {
        ThemeSwitch(
            label = stringResource(R.string.switcher_dark_theme),
            checked = checked,
            onCheckedChange = onCheckedChange
        )

        SettingsItem(
            label = stringResource(R.string.action_share_app),
            iconId = R.drawable.share_lm,
            onClick = onShareApp
        )

        SettingsItem(
            label = stringResource(R.string.support_contact),
            iconId = R.drawable.support_lm,
            onClick = onSupportContact
        )

        SettingsItem(
            label = stringResource(R.string.button_user_agreement),
            iconId = R.drawable.arrow_forward_lm,
            onClick = onOpenUserAgreement
        )
    }
}

@Composable
fun ThemeSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        Text(
            text = label,
            color = MaterialTheme.colorScheme.onPrimary,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = Modifier.align(Alignment.CenterEnd),
            colors = SwitchDefaults.colors(
                uncheckedThumbColor = GrayNeutral,
                uncheckedTrackColor = GrayLight,
                checkedThumbColor = Blue,
                checkedTrackColor = BlueLight
            )
        )
    }
}

@Composable
fun SettingsItem(
    label: String,
    iconId: Int,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = onClick)
            .padding(horizontal = 0.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        Icon(
            painter = painterResource(iconId),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            contentDescription = null,
            modifier = Modifier.align(Alignment.CenterEnd),
        )
    }
}

@Preview(
    name = "Settings light",
    showSystemUi = true,
    showBackground = true
)
@Composable
fun SettingsScreenLightPreview() {
    MyPlaylistMakerTheme(darkTheme = false) {
        Scaffold(
            topBar = { TopBar("Настройки") }
        ) { inner ->
            SettingsScreen(
                checked = false,
                onCheckedChange = { },
                onShareApp = { },
                onSupportContact = { },
                onOpenUserAgreement = { },
                modifier = Modifier.padding(inner)
            )
        }
    }
}

@Preview(
    name = "Settings dark",
    showSystemUi = true,
    showBackground = true
)
@Composable
fun SettingsScreenDarkPreview() {
    MyPlaylistMakerTheme(darkTheme = true) {
        Scaffold(
            topBar = { TopBar("Настройки") }
        ) { inner ->
            SettingsScreen(
                checked = true,
                onCheckedChange = { },
                onShareApp = { },
                onSupportContact = { },
                onOpenUserAgreement = { },
                modifier = Modifier.padding(inner)
            )
        }
    }
}
