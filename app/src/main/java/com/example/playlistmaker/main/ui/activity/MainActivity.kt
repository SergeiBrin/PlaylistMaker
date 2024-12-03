package com.example.playlistmaker.main.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.playlistmaker.R
import com.example.playlistmaker.main.ui.viewmodel.MainViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val mainViewModel: MainViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mainViewModel.getIntentActivityLiveData().observe(this) { event ->
             event.getDataValue()?.let {
                 val intent = Intent(this@MainActivity, it)
                 startActivity(intent)
             }
        }

        val searchButton = findViewById<Button>(R.id.search_button)
        searchButton.setOnClickListener {
             mainViewModel.postSearchActivityIntent()
        }

        val mediaLibraryButton = findViewById<Button>(R.id.media_library_button)
        mediaLibraryButton.setOnClickListener {
            mainViewModel.postMediaLibraryIntent()
        }

        val settingsButton = findViewById<Button>(R.id.settings_button)
        settingsButton.setOnClickListener {
            mainViewModel.postSettingsActivityIntent()
        }
    }
}