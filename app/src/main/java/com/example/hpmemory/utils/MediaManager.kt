package com.example.hpmemory.utils

import android.media.MediaPlayer
import com.example.hpmemory.R


class MediaManager private constructor() {
    lateinit var mediaPlayer:MediaPlayer
    companion object {
        val instance: MediaManager by lazy { MediaManager() }
    }

    fun setVolume(value:Float) {
        mediaPlayer.setVolume(value,value)
    }
    fun start(){
        mediaPlayer.start()
    }
    fun pause(){
        mediaPlayer.pause()
    }

}