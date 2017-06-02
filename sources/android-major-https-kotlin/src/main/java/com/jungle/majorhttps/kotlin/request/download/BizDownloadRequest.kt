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
