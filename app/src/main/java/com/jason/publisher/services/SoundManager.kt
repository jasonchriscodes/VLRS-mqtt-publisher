package com.jason.publisher.services

import android.content.Context
import android.media.MediaPlayer
import java.io.IOException

class SoundManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var isSoundPlaying = false

    fun playSound(soundFileName: String) {
        stopSound()

        mediaPlayer = MediaPlayer()
        try {
            val assetFileDescriptor = context.assets.openFd(soundFileName)
            mediaPlayer?.setDataSource(
                assetFileDescriptor.fileDescriptor,
                assetFileDescriptor.startOffset,
                assetFileDescriptor.length
            )
            mediaPlayer?.prepare()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        mediaPlayer?.setOnCompletionListener {
            isSoundPlaying = false
            releaseMediaPlayer()
        }

        mediaPlayer?.start()
        isSoundPlaying = true
    }

    fun stopSound() {
        if (isSoundPlaying) {
            mediaPlayer?.stop()
            isSoundPlaying = false
            releaseMediaPlayer()
        }
    }

    private fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
