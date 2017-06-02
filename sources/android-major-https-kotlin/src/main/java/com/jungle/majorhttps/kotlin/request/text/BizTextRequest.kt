package com.jungle.majorhttps.kotlin.request.text

import com.android.volley.AuthFailureError
import com.android.volley.NetworkResponse
import com.jungle.majorhttps.kotlin.request.base.BizBaseRequest
import com.jungle.majorhttps.kotlin.request.base.BizRequestListener

class BizTextRequest : BizBaseRequest<String> {

    companion object {

        private val PROTOCOL_CHARSET = "utf-8"
        private val PROTOCOL_CONTENT_TYPE = "application/json; charset=$PROTOCOL_CHARSET"
    }


    private var mRequestBody: ByteArray? = null


    constructor(
            seqId: Int, method: Int, url: String?,
            params: Map<String, *>, headers: Map<String, String>, body: ByteArray?,
            listener: BizRequestListener<String>)
            : super(seqId, method, url, params, headers, listener) {

        mRequestBody = body
    }

    override fun getBodyContentType() = PROTOCOL_CONTENT_TYPE

    @Throws(AuthFailureError::class)
    override fun getBody() = mRequestBody

    override fun parseResponseContent(response: NetworkResponse?): String? {
        return super.parseResponseToStringContent(response)
    }
}