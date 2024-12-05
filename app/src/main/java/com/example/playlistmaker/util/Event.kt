package com.example.playlistmaker.util

class Event<T>(val data: T) {
    var isGetData = false

    fun getDataValue(): T? {
        if (isGetData) {
            return null
        } else {
            isGetData = true
            return data
        }
    }
}