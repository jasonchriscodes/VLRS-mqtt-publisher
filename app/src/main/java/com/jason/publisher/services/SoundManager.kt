package com.jason.publisher.services

import android.content.Context
import android.media.MediaPlayer
import java.io.IOException

/**
 * Class responsible for managing sound playback.
 *
 * @param context The application context.
 */
class SoundManager(private val context: Context) {

    private var mediaPlayer: MediaPlayer? = null
    private var isSoundPlaying = false

    /**
     * Plays a sound from the assets folder.
     *
     * @param soundFileName The name of the sound file to be played.
     */
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

    /**
     * Stops the currently playing sound.
     */
    fun stopSound() {
        if (isSoundPlaying) {
            mediaPlayer?.stop()
            isSoundPlaying = false
            releaseMediaPlayer()
        }
    }

    /**
     * Releases the media player resources.
     */
    private fun releaseMediaPlayer() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
