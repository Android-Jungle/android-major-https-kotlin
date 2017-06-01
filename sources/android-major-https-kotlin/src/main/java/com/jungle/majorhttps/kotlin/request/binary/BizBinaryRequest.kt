package com.jungle.majorhttps.kotlin.request.binary

import com.android.volley.AuthFailureError
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.jungle.majorhttps.kotlin.request.base.BizBaseRequest
import com.jungle.majorhttps.kotlin.request.base.BizBinaryResponse
import com.jungle.majorhttps.kotlin.request.base.BizRequestListener

class BizBinaryRequest : BizBaseRequest<BizBinaryResponse> {

    companion object {

        private val CONTENT_TYPE_BINARY = "application/octet-stream"
        private val CONTENT_TYPE_PROTO_BUF = "application/x-protobuf"
    }


    private var mData: ByteArray? = null


    constructor(
            seqId: Int, method: Int, url: String?,
            params: Map<String, *>, headers: Map<String, String>, data: ByteArray,
            listener: BizRequestListener<BizBinaryResponse>)
            : super(seqId, method, url, params, headers, listener) {

        mData = data
    }

    override fun getBodyContentType() = CONTENT_TYPE_BINARY

    @Throws(AuthFailureError::class)
    override fun getBody() = if (mData != null) mData else super.getBody()

    override fun parseNetworkResponse(response: NetworkResponse?): Response<BizBinaryResponse> {
        val data = if (response != null && response.data != null) response.data else ByteArray(0)
        return Response.success(
                BizBinaryResponse(response, data),
                HttpHeaderParser.parseCacheHeaders(response))
    }
}