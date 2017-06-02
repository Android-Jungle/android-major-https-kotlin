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

package com.jungle.majorhttps.kotlin.request.download

import android.text.TextUtils
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.VolleyError
import com.jungle.majorhttps.kotlin.request.base.BizBaseRequest
import com.jungle.majorhttps.kotlin.request.base.BizBaseResponse
import com.jungle.majorhttps.kotlin.request.base.BizRequestListener
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

class BizDownloadRequest : BizBaseRequest<ByteArray> {

    constructor(
            seqId: Int, method: Int, url: String?,
            params: Map<String, *>, headers: Map<String, String>,
            listener: BizRequestListener<ByteArray>)
            : super(seqId, method, url, params, headers, listener) {
    }

    override fun parseResponseContent(response: NetworkResponse?): ByteArray? {
        return response?.data
    }
}


class BizDownloadFileRequest : BizBaseRequest<String> {

    private var mFilePath: String


    constructor(
            seqId: Int, method: Int, url: String?,
            params: Map<String, *>, headers: Map<String, String>, filePath: String,
            listener: BizRequestListener<String>)
            : super(seqId, method, url, params, headers, listener) {

        mFilePath = filePath
    }

    override fun parseNetworkResponse(response: NetworkResponse?): Response<BizBaseResponse<String>> {
        if (TextUtils.isEmpty(mFilePath)) {
            return Response.error(VolleyError("Download file path must not be null!"))
        }

        val file = File(mFilePath)
        try {
            if (!file.exists()) {
                file.parentFile.mkdirs()
                file.createNewFile()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return Response.error(VolleyError(e.message))
        }

        var success = false
        var stream: BufferedOutputStream? = null

        try {
            stream = BufferedOutputStream(FileOutputStream(file))
            stream.write(response?.data)
            stream.flush()
            success = true
        } catch (e: Exception) {
            return Response.error(VolleyError(e.message))
        } finally {
            if (stream != null) {
                try {
                    stream.close()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        return super.parseNetworkResponse(response)
    }

    override fun parseResponseContent(response: NetworkResponse?): String? {
        return mFilePath
    }
}
