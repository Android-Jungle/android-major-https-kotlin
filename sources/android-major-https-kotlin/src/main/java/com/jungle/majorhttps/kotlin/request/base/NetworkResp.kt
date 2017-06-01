package com.jungle.majorhttps.kotlin.request.base

import com.android.volley.NetworkResponse

class NetworkResp(response: NetworkResponse?) {

    var mStatusCode = 0
    var mHeaders: Map<String, String>? = null
    var mNotModified = false
    var mNetworkTimeMs = 0L


    init {
        if (response != null) {
            mStatusCode = response.statusCode
            mHeaders = response.headers
            mNotModified = response.notModified
            mNetworkTimeMs = response.networkTimeMs
        }
    }

    fun getCookie(): String? {
        return mHeaders?.get("Set-Cookie")
    }
}