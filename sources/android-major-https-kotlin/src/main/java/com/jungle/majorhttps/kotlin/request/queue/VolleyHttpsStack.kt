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
