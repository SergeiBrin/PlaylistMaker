package com.example.playlistmaker.data.network

import com.example.playlistmaker.data.NetworkClient
import com.example.playlistmaker.data.dto.Response
import com.example.playlistmaker.data.dto.TrackDto
import com.example.playlistmaker.data.dto.TrackRequest
import com.example.playlistmaker.data.dto.TrackResponse
import com.example.playlistmaker.data.deserializer.TrackDeserializer
import com.example.playlistmaker.domain.models.Track
import com.example.playlistmaker.util.createRetrofit
import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

class RetrofitNetworkClient : NetworkClient {
    private val baseUrl = "https://itunes.apple.com"

    private val retrofit = createRetrofit(
        baseUrl,
        GsonBuilder()
            .registerTypeAdapter(TrackDto::class.java, TrackDeserializer())
            .create())

    private val itunesApiService = retrofit.create(ItunesApiService::class.java)

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