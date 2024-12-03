package com.example.playlistmaker.settings.ui.activity

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.ui.viewmodel.SettingsViewModel
import com.google.android.material.switchmaterial.SwitchMaterial
import org.koin.androidx.viewmodel.ext.android.viewModel

class SettingsActivity : AppCompatActivity() {
    private val settingsViewModel: SettingsViewModel by viewModel()
    private lateinit var themeSwitcher: SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        themeSwitcher = findViewById(R.id.themeSwitcher)

        settingsViewModel.getThemeSwitcherLiveData().observe(this) { event ->
            event.getDataValue()?.let {
                themeSwitcher.isChecked = it
            }

        }

        settingsViewModel.getSharingLiveData().observe(this) { event ->
            event.getDataValue()?.let {
                startActivity(it)
            }
        }

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            settingsViewModel.switchTheme(checked)
            settingsViewModel.saveThemeSettings()
        }

        val arrowBackButton = findViewById<ImageView>(R.id.arrow_back_settings_screen)
        arrowBackButton.setOnClickListener {
            finish()
        }

        val actionShareAppButton = findViewById<FrameLayout>(R.id.btn_share_app)
        actionShareAppButton.setOnClickListener {
            settingsViewModel.createShareAppIntent(getString(R.string.uri_android_developer))
        }

        val buttonSupportContact = findViewById<FrameLayout>(R.id.btn_support_contact)
        buttonSupportContact.setOnClickListener {
            settingsViewModel.createSupportContactIntent(
                getString(R.string.my_email),
                getString(R.string.letter_subject),
                getString(R.string.letter_text)
            )
        }

        val buttonUserAgreement = findViewById<FrameLayout>(R.id.btn_user_agreement)
        buttonUserAgreement.setOnClickListener {
             settingsViewModel.createUserAgreementIntent(getString(R.string.uri_practicum_offer))
        }
    }
}
