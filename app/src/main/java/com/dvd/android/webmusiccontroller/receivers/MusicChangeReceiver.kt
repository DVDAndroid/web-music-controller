/*
 * Copyright 2017 dvdandroid
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dvd.android.webmusiccontroller.receivers

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioManager
import android.provider.MediaStore
import android.text.Html
import android.util.Base64
import android.view.KeyEvent
import com.dvd.android.webmusiccontroller.servers.sendToClient
import java.io.ByteArrayOutputStream


class MusicChangeReceiver : BroadcastReceiver() {

    companion object {
        val VOLUME_CHANGED_ACTION = "com.dvd.android.webmusiccontroller.VOLUME_CHANGED"
        val SERVER_EVENT_ACTION = "com.dvd.android.webmusiccontroller.SERVER_EVENT"
        val TRACK_CHANGED_ACTION = "com.android.music.playstatechanged"

        val KEY_EVENT = hashMapOf(
                "play_pause" to KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE,
                "next" to KeyEvent.KEYCODE_MEDIA_NEXT,
                "previous" to KeyEvent.KEYCODE_MEDIA_PREVIOUS
        )

        var lastSong: String? = null
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        val audio = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        when (action) {
            VOLUME_CHANGED_ACTION -> {
                val currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC)
                sendToClient("VOLUME: $currentVolume")
            }
            TRACK_CHANGED_ACTION -> {
                val duration = intent.getLongExtra("duration", 0)
                if (duration == 0L) return

                var artist = intent.getStringExtra("artist")
                val playing = intent.getBooleanExtra("playing", false)
                var album = intent.getStringExtra("album")
                val track = intent.getStringExtra("track")

                if (artist.contains("unknown")) artist = Html.escapeHtml(artist)
                if (album.contains("unknown")) album = Html.escapeHtml(album)

                if (lastSong != track) {
                    sendToClient("ALBUM: $album &#8226; $artist")
                    sendToClient("TRACK: $track")
                    sendToClient("ALBUM_ART: ${albumArtBase64(context, album)}")
                }
                sendToClient("PLAYING: $playing")

                lastSong = track
            }
            SERVER_EVENT_ACTION -> {
                val event = intent.extras.getString("event")
                val extra = intent.extras.getString("extra")
                when (event!!) {
                    in KEY_EVENT -> audio.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KEY_EVENT[event]!!))
                    "volume" -> {
                        val newVolume = extra.toInt()
                        audio.setStreamVolume(AudioManager.STREAM_MUSIC, newVolume, AudioManager.FLAG_SHOW_UI)
                    }
                    "reload" -> {
                        lastSong = null

                        val currentVolume = audio.getStreamVolume(AudioManager.STREAM_MUSIC)
                        sendToClient("VOLUME: $currentVolume")

                        audio.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE))
                        audio.dispatchMediaKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE))
                    }
                }
            }
        }
    }

    fun albumArtBase64(context: Context, album: String): String? {
        try {
            val cursorAlbum = context.contentResolver.query(MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                    arrayOf(MediaStore.Audio.Albums.ALBUM_ART), "${MediaStore.Audio.Albums.ALBUM} = \"$album\"", null, null)
            cursorAlbum?.moveToFirst()
            val path = cursorAlbum.getString(0)
            cursorAlbum.close()

            if (path.isNullOrEmpty()) return "./res/default_album_art.png"

            val bitmap = BitmapFactory.decodeFile(path)
            val bao = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bao)
            val art = Base64.encodeToString(bao.toByteArray(), Base64.DEFAULT)
            return "data:image/png;base64, $art"
        } catch (ignored: Throwable) {
            return "./res/default_album_art.png"
        }
    }

}