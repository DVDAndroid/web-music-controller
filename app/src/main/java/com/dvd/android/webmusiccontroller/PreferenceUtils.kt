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

package com.dvd.android.webmusiccontroller

import android.content.Context
import android.content.SharedPreferences

class PreferenceUtils(context: Context) {

    var prefs: SharedPreferences? = null

    init {
        if (prefs == null) prefs = context.getSharedPreferences(this::class.java.`package`.name, Context.MODE_PRIVATE)
    }

    fun isFirstStart(): Boolean {
        val firstStart = prefs!!.getBoolean("first_start", true)
        prefs!!.edit().putBoolean("first_start", false).apply()
        return firstStart
    }

    fun getUsername(): String = prefs!!.getString("auth_username", "")
    fun getPassword(): String = prefs!!.getString("auth_password", "")

    fun setUsername(username: String): PreferenceUtils {
        prefs!!.edit().putString("auth_username", username).apply()
        return this
    }

    fun setPassword(password: String): PreferenceUtils {
        prefs!!.edit().putString("auth_password", password).apply()
        return this
    }

    fun isAuthEnabled(): Boolean = prefs!!.getBoolean("auth", false)
    fun setAuthEnabled(enabled: Boolean): PreferenceUtils {
        prefs!!.edit().putBoolean("auth", enabled).apply()
        return this
    }
}