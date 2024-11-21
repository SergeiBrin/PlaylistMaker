package com.example.playlistmaker.sharing

import android.content.Intent
import android.net.Uri

fun getShareAppIntent(url: String): Intent {
    return Intent().apply {
        action = Intent.ACTION_SEND
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT, url)
    }
}

fun getSupportContactIntent(email: String, subject: String, text: String): Intent {
    return Intent().apply {
        action = Intent.ACTION_SENDTO
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, text)
    }
}

fun getUserAgreementIntent(url: String): Intent {
    val uri = Uri.parse(url)
    return Intent(Intent.ACTION_VIEW, uri)
}