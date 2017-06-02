package com.jungle.majorhttps.kotlin.request.binary

import com.android.volley.AuthFailureError
import com.android.volley.NetworkResponse
import com.jungle.majorhttps.kotlin.request.base.BizBaseRequest
import com.jungle.majorhttps.kotlin.request.base.BizRequestListener

class BizBinaryRequest : BizBaseRequest<ByteArray> {

    companion object {

        private val CONTENT_TYPE_BINARY = "application/octet-stream"
        private val CONTENT_TYPE_PROTO_BUF = "application/x-protobuf"
    }


    private var mData: ByteArray? = null


    constructor(
            seqId: Int, method: Int, url: String?,
            params: Map<String, *>, headers: Map<String, String>, data: ByteArray,
            listener: BizRequestListener<ByteArray>)
            : super(seqId, method, url, params, headers, listener) {

        mData = data
    }

    override fun getBodyContentType() = CONTENT_TYPE_BINARY

    @Throws(AuthFailureError::class)
    override fun getBody() = if (mData != null) mData else super.getBody()

    override fun parseResponseContent(response: NetworkResponse?): ByteArray? {
        return response?.data
    }
}