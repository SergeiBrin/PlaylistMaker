package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TrackRequest
import com.example.playlistmaker.search.data.dto.TrackResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.IOException

class RetrofitNetworkClient(
    private val itunesApiService: ItunesApiService
) : NetworkClient {

    override suspend fun doRequest(dto: Any): Response {
        return if (dto is TrackRequest) {
            withContext(Dispatchers.IO) {
                try {
                    val resp = itunesApiService.search(dto.searchText)
                    val body = TrackResponse(resp.tracks)
                    body.apply { resultCode = 200 }
                } catch (e: IOException) { // Нет сети
                    Response().apply { resultCode = 503 }
                }
            }
        } else {
            Response().apply { resultCode = 400 }
        }
    }
}