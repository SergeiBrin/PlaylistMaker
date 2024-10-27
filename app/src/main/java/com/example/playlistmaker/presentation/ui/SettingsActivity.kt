package com.example.playlistmaker.presentation.ui

import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.Creator
import com.example.playlistmaker.R
import com.example.playlistmaker.domain.impl.SettingsInteractorImpl
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsActivity : AppCompatActivity() {
    private val settingsInteractor = SettingsInteractorImpl()
    private val themeInteractor = Creator.provideThemeInteractor()
    private lateinit var themeSwitcher: SwitchMaterial

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val arrowBackButton = findViewById<ImageView>(R.id.arrow_back_settings_screen)
        arrowBackButton.setOnClickListener {
            finish()
        }

        themeSwitcher = findViewById(R.id.themeSwitcher)
        setSwitchState()

        themeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            themeInteractor.switchTheme(checked)
            themeInteractor.saveThemeSettings()
        }

        val actionShareAppButton = findViewById<FrameLayout>(R.id.btn_share_app)
        actionShareAppButton.setOnClickListener {
            val intent = settingsInteractor.getShareAppIntent(getString(R.string.uri_android_developer))
            startActivity(intent)
        }

        val buttonSupportContact = findViewById<FrameLayout>(R.id.btn_support_contact)
        buttonSupportContact.setOnClickListener {
            val intent = settingsInteractor.getSupportContactIntent(
                getString(R.string.my_email),
                getString(R.string.letter_subject),
                getString(R.string.letter_text)
            )
            startActivity(intent)
        }

        val buttonUserAgreement = findViewById<FrameLayout>(R.id.btn_user_agreement)
        buttonUserAgreement.setOnClickListener {
            val intent = settingsInteractor.getUserAgreementIntent(getString(R.string.uri_practicum_offer))
            startActivity(intent)
        }
    }

    private fun setSwitchState() {
        themeSwitcher.post {
            themeSwitcher.isChecked = themeInteractor.getIsThemeDark()
        }
    }
}
