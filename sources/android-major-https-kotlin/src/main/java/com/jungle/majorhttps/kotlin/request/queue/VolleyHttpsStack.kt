/**
 * Android Jungle-Major-Https-Kotlin framework project.
 *
 * Copyright 2017 Arno Zhang <zyfgood12@163.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jungle.majorhttps.kotlin.request.queue

import com.android.volley.toolbox.HurlStack
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLSocketFactory


class VolleyHttpsStack : HurlStack {

    private var mHostnameVerifier: HostnameVerifier? = null

    constructor() : this(null) {}

    constructor(urlRewriter: UrlRewriter?) : this(urlRewriter, null) {}

    constructor(
            urlRewriter: UrlRewriter?,
            factory: SSLSocketFactory?)
            : super(urlRewriter, factory) {
    }

    constructor(
            urlRewriter: UrlRewriter?,
            sslSocketFactory: SSLSocketFactory?,
            verifier: HostnameVerifier?)
            : super(urlRewriter, sslSocketFactory) {

        mHostnameVerifier = verifier
    }

    @Throws(IOException::class)
    override fun createConnection(url: URL): HttpURLConnection {
        val connection = super.createConnection(url)
        if (mHostnameVerifier != null && "https" == url.protocol) {
            (connection as HttpsURLConnection).hostnameVerifier = mHostnameVerifier
        }

        return connection
    }
}
