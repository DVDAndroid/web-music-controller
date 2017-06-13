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

import com.dvd.android.webmusiccontroller.receivers.MusicChangeReceiver.Companion.VOLUME_CHANGED_ACTION

class VolumeContentObserver(private val context: android.content.Context) : android.database.ContentObserver(android.os.Handler()) {

    private val MEDIA_VOLUME_CHANGE = "content://settings/system/volume_music_speaker"
    private val MEDIA_VOLUME_HEADPHONES_CHANGE = "content://settings/system/volume_music_headphone"

    override fun onChange(selfChange: Boolean, uri: android.net.Uri) {
        super.onChange(selfChange, uri)
        when (uri.toString()) {
            MEDIA_VOLUME_CHANGE,
            MEDIA_VOLUME_HEADPHONES_CHANGE -> context.sendBroadcast(android.content.Intent(VOLUME_CHANGED_ACTION))
            else -> return
        }
    }

}