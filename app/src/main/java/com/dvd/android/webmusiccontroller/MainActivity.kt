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
import android.content.Intent
import android.net.Uri
import android.net.wifi.WifiManager
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.AppCompatActivity
import android.text.format.Formatter
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.dvd.android.webmusiccontroller.receivers.VolumeContentObserver
import com.dvd.android.webmusiccontroller.servers.*
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (PreferenceUtils(this).isFirstStart()) {
            startActivity(Intent(this@MainActivity, MainIntroActivity::class.java))
        }

        startAll(this, 9620, 9621)
        checkServers()

        contentResolver.registerContentObserver(android.provider.Settings.System.CONTENT_URI, true, VolumeContentObserver(this))
        webserver_container.setOnClickListener {
            if (isWebServerRunning()) {
                Snackbar.make(webserver_container, R.string.webserver_running, Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            startWebServer(this, 9620)
            runOnUiThread { Handler().postDelayed({ checkServers() }, 1000) }
        }

        websocket_container.setOnClickListener {
            if (isWebSocketRunning()) {
                Snackbar.make(websocket_container, R.string.websocket_running, Snackbar.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            startWebSocket(this, 9621)
            runOnUiThread { Handler().postDelayed({ checkServers() }, 1000) }
        }

        authentication_enabled.isChecked = PreferenceUtils(this).isAuthEnabled()
        authentication_enabled.setOnCheckedChangeListener { _, isChecked ->
            PreferenceUtils(this).setAuthEnabled(isChecked)
        }

        username.setText(PreferenceUtils(this).getUsername())
        password.setText(PreferenceUtils(this).getPassword())

        save_credentials.setOnClickListener {
            if (username.text.isEmpty()) {
                input_username.error = getString(R.string.blank_field)
                input_username.isErrorEnabled = true
            }

            if (password.text.isEmpty()) {
                input_password.error = getString(R.string.blank_field)
                input_password.isErrorEnabled = true
                return@setOnClickListener
            }

            PreferenceUtils(this)
                    .setUsername(username.text.toString())
                    .setPassword(password.text.toString())

            Snackbar.make(save_credentials, R.string.credentials_saved, Snackbar.LENGTH_SHORT).show()

        }

        if (getIp() == null) {
            no_internet.visibility = View.VISIBLE
            internet_usage.visibility = View.GONE
        } else {
            internet_usage.visibility = View.VISIBLE
            ip_address.text = "http://${getIp()}:9620"
        }

        ip_address.setOnClickListener {
            val i = Intent(Intent.ACTION_VIEW)
            i.data = Uri.parse(ip_address.text.toString())
            startActivity(i)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.start_all -> startAll(this@MainActivity, 9620, 9621)
            R.id.stop_all -> stopAll()
        }
        checkServers()

        return true
    }

    private fun checkServers() {
        runOnUiThread {
            Handler().postDelayed({
                if (isWebServerRunning()) {
                    webserver_icon.setImageResource(R.drawable.ic_ok)
                    webserver_status.setText(R.string.webserver_running)
                } else {
                    webserver_icon.setImageResource(R.drawable.ic_error)
                    webserver_status.setText(R.string.webserver_not_running)
                }

                if (isWebSocketRunning()) {
                    websocket_icon.setImageResource(R.drawable.ic_ok)
                    websocket_status.setText(R.string.websocket_running)
                } else {
                    websocket_icon.setImageResource(R.drawable.ic_error)
                    websocket_status.setText(R.string.websocket_not_running)
                }

            }, 1000)
        }
    }

    @Suppress("DEPRECATION")
    private fun getIp(): String? {
        val wm = getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ip = Formatter.formatIpAddress(wm.connectionInfo.ipAddress)
        if (ip == "0.0.0.0") return null
        else return ip
    }

}