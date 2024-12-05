package com.example.playlistmaker.search.data.network

import com.example.playlistmaker.search.data.dto.Response
import com.example.playlistmaker.search.data.dto.TrackRequest
import java.io.IOException

class RetrofitNetworkClient(
    private val itunesApiService: ItunesApiService
) : NetworkClient {

    override fun doRequest(dto: Any): Response {
        if (dto is TrackRequest) {
            try {
                val resp = itunesApiService.search(dto.searchText).execute()
                val body = resp.body() ?: Response()
                return body.apply { resultCode = resp.code() }
            } catch (e: IOException) { // Нет сети
                return Response().apply { resultCode = 503 }
            }
        } else {
            return Response().apply { resultCode = 400 }
        }
    }
}