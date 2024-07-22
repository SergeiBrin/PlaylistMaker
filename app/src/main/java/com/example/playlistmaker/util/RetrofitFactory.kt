package com.example.playlistmaker.util

import com.google.gson.Gson
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun createRetrofit(baseUrl: String, gson: Gson? = null): Retrofit {
    val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)

    if (gson != null) {
        retrofit.addConverterFactory(GsonConverterFactory.create(gson))
    } else {
        retrofit.addConverterFactory(GsonConverterFactory.create())
    }

    return retrofit.build()
}