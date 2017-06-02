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