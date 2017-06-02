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

package com.jungle.majorhttps.kotlin.request.base

import android.text.TextUtils
import com.android.volley.NetworkResponse
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import java.net.URLEncoder
import java.util.*

abstract class BizBaseRequest<T> : Request<BizBaseResponse<T>> {

    protected var mSeqId: Int = 0
    private var mRedirectUrl: String? = null
    protected var mRequestParams: Map<String, *>? = null
    protected var mRequestHeaders: Map<String, String>? = null
    protected var mExtraHeaderFiller: ExtraHeadersFiller? = null
    protected var mListener: BizRequestListener<T>? = null


    constructor(
            seqId: Int, method: Int, url: String?,
            params: Map<String, *>?, headers: Map<String, String>,
            listener: BizRequestListener<T>)
            : super(method, url, null) {

        mSeqId = seqId
        mListener = listener
        mRequestParams = params
        mRequestHeaders = headers

        redirectRequest()
    }

    fun setExtraHeadersFiller(filler: ExtraHeadersFiller?) {
        mExtraHeaderFiller = filler
    }

    override fun getUrl(): String
            = if (!TextUtils.isEmpty(mRedirectUrl)) mRedirectUrl!! else super.getUrl()

    fun getOriginalUrl(): String = super.getUrl()

    override fun getHeaders(): MutableMap<String, String> {
        val headers = HashMap<String, String>()
        if (mRequestHeaders != null) {
            headers.putAll(mRequestHeaders!!)
        }

        mExtraHeaderFiller?.fillHeaders(headers)
        return super.getHeaders()
    }

    override fun getPostBody(): ByteArray {
        return super.getBody()
    }

    @Suppress("UNCHECKED_CAST")
    override fun getParams(): MutableMap<String, String> {
        return mRequestParams as MutableMap<String, String>
    }

    override fun parseNetworkResponse(response: NetworkResponse?): Response<BizBaseResponse<T>> {
        return Response.success(
                createBizResponse(response),
                HttpHeaderParser.parseCacheHeaders(response))
    }

    protected fun createBizResponse(response: NetworkResponse?): BizBaseResponse<T> {
        return BizBaseResponse<T>(response, parseResponseContent(response))
    }

    protected abstract fun parseResponseContent(response: NetworkResponse?): T?

    protected fun parseResponseToStringContent(response: NetworkResponse?): String {
        var content: String = ""
        try {
            if (response != null && response.data != null) {
                // TODO: use charset: content = response.data.toString(charset)
                //
                val charset = HttpHeaderParser.parseCharset(response.headers)
                content = response.data.toString()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return content
    }

    override fun deliverResponse(response: BizBaseResponse<T>) {
        mListener?.onSuccess(mSeqId, response)
    }

    override fun deliverError(error: VolleyError?) {
        mListener?.onError(mSeqId, error)
    }

    protected fun redirectRequest() {
        if (method != Method.GET) {
            return
        }

        if (mRequestParams == null || mRequestParams!!.isEmpty()) {
            return
        }

        var url = getOriginalUrl()
        if (TextUtils.isEmpty(url)) {
            return
        }

        val encodeParams = encodeParameters(paramsEncoding)
        if (!TextUtils.isEmpty(encodeParams)) {
            url = if (!url.contains("?")) "$url?$encodeParams" else "$url&$encodeParams"
        }

        mRedirectUrl = url
    }

    protected fun encodeParameters(encoding: String): String? {
        val builder = StringBuilder()

        try {
            if (mRequestParams != null && !mRequestParams!!.isEmpty()) {
                for ((key, value) in mRequestParams!!) {
                    builder.append(URLEncoder.encode(key, encoding))
                    builder.append('=');
                    builder.append(URLEncoder.encode(value.toString(), encoding))
                    builder.append("&")
                }

                builder.deleteCharAt(builder.length - 1)
            }

            return builder.toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }
}
