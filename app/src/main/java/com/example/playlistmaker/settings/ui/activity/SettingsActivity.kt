package com.example.playlistmaker.settings.ui.activity

import android.content.Intent
import android.net.Uri
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

        settingsViewModel.getThemeSwitcherLiveData().observe(this) { checked ->
            themeSwitcher.isChecked = checked
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
            val shareAppIntent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, getString(R.string.uri_android_developer))
            }

            startActivity(shareAppIntent)
        }

        val buttonSupportContact = findViewById<FrameLayout>(R.id.btn_support_contact)
        buttonSupportContact.setOnClickListener {
            val supportContactIntent = Intent().apply {
                action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.my_email)))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.letter_subject))
                putExtra(Intent.EXTRA_TEXT, getString(R.string.letter_text))
            }

            startActivity(supportContactIntent)
        }

        val buttonUserAgreement = findViewById<FrameLayout>(R.id.btn_user_agreement)
        buttonUserAgreement.setOnClickListener {
            val uri = Uri.parse(getString(R.string.uri_practicum_offer))
            val userAgreementIntent = Intent().apply {
                action = Intent.ACTION_VIEW
                data = uri
            }

            startActivity(userAgreementIntent)
        }
    }
}
