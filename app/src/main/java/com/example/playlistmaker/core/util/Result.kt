package com.example.playlistmaker.core.util

sealed class Result {
    data object Success : Result()
    data object Failure : Result()
}