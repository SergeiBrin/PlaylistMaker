package com.example.playlistmaker.settings.ui.activity

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.playlistmaker.R
import com.example.playlistmaker.settings.ui.viewmodel.ThemeViewModel
import com.example.playlistmaker.sharing.getShareAppIntent
import com.example.playlistmaker.sharing.getSupportContactIntent
import com.example.playlistmaker.sharing.getUserAgreementIntent
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    private lateinit var themeViewModel: ThemeViewModel
    private lateinit var themeSwitcher: SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        themeSwitcher = findViewById(R.id.themeSwitcher)

        themeViewModel = ViewModelProvider(this, ThemeViewModel.getViewModelFactory())[ThemeViewModel::class.java]

        themeViewModel.getThemeSwitcherLiveData().observe(this) { isChecked ->
            themeSwitcher.isChecked = isChecked
        }

        themeSwitcher.setOnCheckedChangeListener { _, checked ->
            themeViewModel.switchTheme(checked)
            themeViewModel.saveThemeSettings()
        }

        val arrowBackButton = findViewById<ImageView>(R.id.arrow_back_settings_screen)
        arrowBackButton.setOnClickListener {
            finish()
        }

        val actionShareAppButton = findViewById<FrameLayout>(R.id.btn_share_app)
        actionShareAppButton.setOnClickListener {
            val intent = getShareAppIntent(getString(R.string.uri_android_developer))
            startActivity(intent)
        }

        val buttonSupportContact = findViewById<FrameLayout>(R.id.btn_support_contact)
        buttonSupportContact.setOnClickListener {
            val intent = getSupportContactIntent(
                getString(R.string.my_email),
                getString(R.string.letter_subject),
                getString(R.string.letter_text)
            )
            startActivity(intent)
        }

        val buttonUserAgreement = findViewById<FrameLayout>(R.id.btn_user_agreement)
        buttonUserAgreement.setOnClickListener {
            val intent = getUserAgreementIntent(getString(R.string.uri_practicum_offer))
            startActivity(intent)
        }
    }
}
