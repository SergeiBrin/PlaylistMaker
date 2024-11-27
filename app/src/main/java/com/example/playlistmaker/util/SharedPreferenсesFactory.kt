package com.example.playlistmaker.util

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences

fun getThemeSettings(context: Context): SharedPreferences {
    return context.getSharedPreferences("PlaylistMakerSettings", MODE_PRIVATE)
}

fun getSearchHistorySharedPref(context: Context): SharedPreferences {
    return context.getSharedPreferences("SearchHistory", MODE_PRIVATE)
}



