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
import com.dvd.android.webmusiccontroller.PreferenceUtils
import org.java_websocket.util.Base64
import org.nanohttpd.protocols.http.IHTTPSession
import org.nanohttpd.protocols.http.NanoHTTPD
import org.nanohttpd.protocols.http.response.Response
import org.nanohttpd.protocols.http.response.Response.newFixedLengthResponse
import org.nanohttpd.protocols.http.response.Status


class WebServer(val context: Context, port: Int) : NanoHTTPD(port) {

    //    private lateinit var keyStore: KeyStore
    private val MIME_TYPES = hashMapOf(
            "html" to NanoHTTPD.MIME_HTML,
            "css" to "text/css",
            "js" to "text/javascript",
            "jpg" to "image/jpeg",
            "png" to "image/png",
            "ico" to "image/x-icon"
    )

    init {
        addHTTPInterceptor { serve(it) }

        /* TODO
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore")
            keyStore.load(null)

            val aliases = keyStore.aliases()
            val keyAliases = mutableListOf<String>()
            while (aliases.hasMoreElements()) {
                keyAliases.add(aliases.nextElement())
            }

            var alias = prefs.getString("alias", "")
            if (alias == "") {
                alias = UUID.randomUUID().toString()
                prefs.edit().putString("alias", alias).apply()
            }

            val genNewKey = !keyAliases.contains(alias)
            if (genNewKey) {
                generateKeys(alias)
                keyStore = KeyStore.getInstance("AndroidKeyStore")
                keyStore.load(null)
            }
        } catch (t: Throwable) {
            t.printStackTrace()
        }

        if (prefs.getBoolean("auth", true)) {
            val keyManagerFactory: KeyManagerFactory?
            try {
                keyManagerFactory = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm())
                keyManagerFactory!!.init(keyStore, "".toCharArray())

                makeSecure(NanoHTTPD.makeSSLSocketFactory(keyStore, keyManagerFactory), null)
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            } catch (e: UnrecoverableKeyException) {
                e.printStackTrace()
            } catch (e: KeyStoreException) {
                e.printStackTrace()
            } catch (t: Throwable) {
                t.printStackTrace()
            }
        }
        */
    }

    @Suppress("OverridingDeprecatedMember")
    override fun serve(session: IHTTPSession?): Response {
        var uri = session!!.uri
        if (PreferenceUtils(context).isAuthEnabled()) {
            val headers = session.headers

            val base64 = headers["authorization"]?.substring(6) ?: ""
            val credentials = "${PreferenceUtils(context).getUsername()}:${PreferenceUtils(context).getPassword()}"
            val logged = base64 == Base64.encodeBytes(credentials.toByteArray())

            if (!logged) {
                val res = newFixedLengthResponse(Status.UNAUTHORIZED, NanoHTTPD.MIME_HTML, "UNAUTHORIZED")
                res.addHeader("WWW-Authenticate", "Basic realm=\"Authorization\"")
                res.addHeader("Content-Length", "0")
                return res
            }
        }

        if (uri == "/") uri = "index.html"
        else uri = uri.substring(1)

        val content = context.assets.open(uri).readBytes()
        val ext = uri.substring(uri.lastIndexOf(".") + 1)

        return newFixedLengthResponse(Status.OK, MIME_TYPES[ext] ?: "application/octet-stream", content)
    }

    /* TODO
    fun generateKeys(alias: String): KeyPair? {
        var keyPair: KeyPair? = null
        try {
            val keyGen = KeyPairGenerator.getInstance("RSA", "AndroidKeyStore")

            val start = Calendar.getInstance()
            val end = Calendar.getInstance()
            end.add(Calendar.YEAR, 1)

            if (Build.VERSION.SDK_INT > 23) {
                val spec = KeyGenParameterSpec.Builder(alias, KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY)
                        .setCertificateSubject(X500Principal("CN=WebMusicController, OU=dvdandroid, O=dvdandroid, C=IT"))
                        .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                        .setSignaturePaddings(KeyProperties.SIGNATURE_PADDING_RSA_PKCS1)
                        .setCertificateNotBefore(start.time)
                        .setCertificateNotAfter(end.time)
                        .setKeyValidityStart(start.time)
                        .setKeyValidityEnd(end.time)
                        .setKeySize(2048)
                        .setCertificateSerialNumber(BigInteger.valueOf(1))
                        .build()

                keyGen.initialize(spec)
            } else {
                val spec = KeyPairGeneratorSpec.Builder(context)
                        .setAlias(alias)
                        .setSubject(X500Principal("CN=WebMusicController, OU=dvdandroid, O=dvdandroid, C=IT"))
                        .setSerialNumber(BigInteger.valueOf(12345))
                        .setStartDate(start.time)
                        .setEndDate(end.time)
                        .build()

                keyGen.initialize(spec)
            }

            keyPair = keyGen.generateKeyPair()
        } catch (e: GeneralSecurityException) {
            e.printStackTrace()
        }

        return keyPair
    }
    */

}