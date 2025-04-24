package ai.elimu.kukariri.util

import android.content.Context
import android.media.MediaPlayer
import android.util.Log

/**
 * Utility class which helps releasing the [MediaPlayer] instance after
 * finishing playing the audio.
 *
 *
 *
 * See https://developer.android.com/reference/android/media/MediaPlayer.html#create%28android.content.Context,%20int%29
 */
object MediaPlayerHelper {
    fun play(context: Context?, resId: Int) {
        Log.i(MediaPlayerHelper::class.java.name, "play")

        val mediaPlayer = MediaPlayer.create(context, resId)
        mediaPlayer.setOnCompletionListener { mediaPlayer.release() }
        mediaPlayer.start()
    }
}
