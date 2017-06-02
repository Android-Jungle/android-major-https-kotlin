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