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

var webServer: WebServer? = null
var webSocket: WebSocket? = null

fun startAll(context: Context, port1: Int, port2: Int) {
    if (!isWebServerRunning()) startWebServer(context, port1)
    if (!isWebSocketRunning()) startWebSocket(context, port2)
}

fun startWebServer(context: Context, port1: Int) {
    webServer = WebServer(context, port1)
    webServer!!.start()
}

fun startWebSocket(context: Context, port2: Int) {
    webSocket = WebSocket(context, port2)

    webSocket!!.start()
}

fun stopAll() {
    webServer?.closeAllConnections()
    webSocket?.stop()

    webServer = null
    webSocket = null
}

fun sendToClient(string: String) = webSocket?.send(string)

fun isWebServerRunning(): Boolean = webServer != null && webServer!!.isAlive
fun isWebSocketRunning(): Boolean = webSocket != null && webSocket!!.running