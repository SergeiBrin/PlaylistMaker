package com.example.playlistmaker.search.domain.common

import com.example.playlistmaker.core.model.Track

sealed interface Resource {
    data class Success(val data: List<Track>) : Resource
    object Error : Resource
}