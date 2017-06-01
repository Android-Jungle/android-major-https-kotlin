package com.jungle.majorhttps.kotlin.request.text

import com.android.volley.AuthFailureError
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.jungle.majorhttps.kotlin.request.base.BizBaseRequest
import com.jungle.majorhttps.kotlin.request.base.BizRequestListener
import com.jungle.majorhttps.kotlin.request.base.BizTextResponse

class BizTextRequest : BizBaseRequest<BizTextResponse> {

    companion object {

        private val PROTOCOL_CHARSET = "utf-8"
        private val PROTOCOL_CONTENT_TYPE = "application/json; charset=$PROTOCOL_CHARSET"
    }


    private var mRequestBody: ByteArray? = null


    constructor(
            seqId: Int, method: Int, url: String?,
            params: Map<String, *>, headers: Map<String, String>, body: ByteArray?,
            listener: BizRequestListener<BizTextResponse>)
            : super(seqId, method, url, params, headers, listener) {

        mRequestBody = body
    }

    override fun getBodyContentType() = PROTOCOL_CONTENT_TYPE

    @Throws(AuthFailureError::class)
    override fun getBody() = mRequestBody

    override fun parseNetworkResponse(response: NetworkResponse?): Response<BizTextResponse> {
        return Response.success(
                BizTextResponse(response, getResponseContent(response)),
                HttpHeaderParser.parseCacheHeaders(response))
    }
}