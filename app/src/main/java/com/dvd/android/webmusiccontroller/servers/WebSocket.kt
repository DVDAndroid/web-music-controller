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

package com.dvd.android.webmusiccontroller.servers

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.dvd.android.webmusiccontroller.R
import com.dvd.android.webmusiccontroller.receivers.MusicChangeReceiver
import org.java_websocket.WebSocket
import org.java_websocket.exceptions.WebsocketNotConnectedException
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.lang.Exception
import java.net.InetSocketAddress

class WebSocket(val context: Context, port: Int) : WebSocketServer(InetSocketAddress(port)) {

    var running = false

    override fun onStart() {
        running = true
    }

    override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
        running = true
    }

    override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        running = false
    }

    override fun onMessage(conn: WebSocket?, message: String?) {
        val intent = Intent(MusicChangeReceiver.SERVER_EVENT_ACTION)
        intent.putExtra("event", message)
        if (message!!.contains("=")) {
            intent.putExtra("event", message.split("=")[0])
            intent.putExtra("extra", message.split("=")[1])
        }

        context.sendBroadcast(intent)
    }

    override fun onError(conn: WebSocket?, ex: Exception?) {
        running = false
        ex!!.printStackTrace()
        Toast.makeText(context, R.string.no_server_connection, Toast.LENGTH_LONG).show()
    }

    fun send(text: String) {
        try {
            val con = connections()
            synchronized(con) { for (c in con) c.send(text) }
        } catch (e: WebsocketNotConnectedException) {
        }
    }

}