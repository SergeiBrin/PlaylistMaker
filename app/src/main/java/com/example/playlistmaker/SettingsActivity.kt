package com.example.playlistmaker

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val arrowBackButton = findViewById<ImageView>(R.id.arrow_back_settings_screen)
        arrowBackButton.setOnClickListener {
            finish()
        }

        val actionShareAppButton = findViewById<FrameLayout>(R.id.btn_share_app)
        actionShareAppButton.setOnClickListener {
            val url = getString(R.string.uri_android_developer)

            val intent = Intent().apply {
                action = Intent.ACTION_SEND
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, url)
            }

            startActivity(intent)
        }

        val buttonSupportContact = findViewById<FrameLayout>(R.id.btn_support_contact)
        buttonSupportContact.setOnClickListener {
            val email = getString(R.string.my_email)
            val subject = getString(R.string.letter_subject)
            val text = getString(R.string.letter_text)

            val intent = Intent().apply {
                action = Intent.ACTION_SENDTO
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_SUBJECT, subject)
                putExtra(Intent.EXTRA_TEXT, text)
            }

            startActivity(intent)
        }

        val buttonUserAgreement = findViewById<FrameLayout>(R.id.btn_user_agreement)
        buttonUserAgreement.setOnClickListener {
            val urlOffer = getString(R.string.uri_practicum_offer)
            val uri = Uri.parse(urlOffer)
            val intent = Intent(Intent.ACTION_VIEW, uri)

            startActivity(intent)
        }
    }
}
