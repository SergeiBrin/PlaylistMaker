package com.example.playlistmaker.main.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.playlistmaker.util.Event
import com.example.playlistmaker.medialibrary.ui.activity.MediaLibraryActivity
import com.example.playlistmaker.search.ui.activity.SearchActivity
import com.example.playlistmaker.settings.ui.activity.SettingsActivity

class MainViewModel : ViewModel() {

    private val classActivityLiveData = MutableLiveData<Event<Class<*>>>()

    fun getIntentActivityLiveData(): LiveData<Event<Class<*>>> = classActivityLiveData

    fun postSearchActivityIntent() {
        classActivityLiveData.postValue(Event(SearchActivity::class.java))
    }

    fun postMediaLibraryIntent() {
        classActivityLiveData.postValue(Event(MediaLibraryActivity::class.java))
    }

    fun postSettingsActivityIntent() {
        classActivityLiveData.postValue(Event(SettingsActivity::class.java))
    }
}