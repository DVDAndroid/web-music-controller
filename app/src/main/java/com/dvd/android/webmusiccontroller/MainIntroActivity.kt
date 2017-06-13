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

import android.os.Build
import android.os.Bundle
import com.heinrichreimersoftware.materialintro.app.IntroActivity
import com.heinrichreimersoftware.materialintro.slide.SimpleSlide

class MainIntroActivity : IntroActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        isButtonBackVisible = false
        isButtonNextVisible = false
        buttonCtaTintMode = BUTTON_CTA_TINT_MODE_TEXT
        pageScrollDuration = 500

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            setPageScrollInterpolator(android.R.interpolator.fast_out_slow_in)

        addSlide(SimpleSlide.Builder()
                .title(R.string.app_name)
                .description(R.string.welcome_description)
                .image(R.drawable.ic_launcher_large)
                .background(R.color.slide_1)
                .backgroundDark(R.color.slide_1_dark)
                .layout(R.layout.slide)
                .build())

        addSlide(SimpleSlide.Builder()
                .title(R.string.connection_type)
                .description(R.string.connection_type_d)
                .image(R.drawable.ic_usb_wifi)
                .background(R.color.slide_2)
                .backgroundDark(R.color.slide_2_dark)
                .layout(R.layout.slide)
                .build())

        addSlide(SimpleSlide.Builder()
                .title(R.string.authorization_available)
                .description(R.string.authorization_available_d)
                .image(R.drawable.ic_secure_access)
                .background(R.color.slide_3)
                .backgroundDark(R.color.slide_3_dark)
                .layout(R.layout.slide)
                .build())

        addSlide(SimpleSlide.Builder()
                .title(R.string.storage_access)
                .description(R.string.storage_access_d)
                .image(R.drawable.ic_storage_album)
                .background(R.color.slide_4)
                .backgroundDark(R.color.slide_4_dark)
                .layout(R.layout.slide)
                .permission(android.Manifest.permission.READ_EXTERNAL_STORAGE)
                .build())
    }

}