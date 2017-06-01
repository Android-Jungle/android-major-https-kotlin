package com.jungle.majorhttps.kotlin.request.download

import android.text.TextUtils
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.HttpHeaderParser
import com.jungle.majorhttps.kotlin.request.base.BizBaseRequest
import com.jungle.majorhttps.kotlin.request.base.BizDownloadFileResponse
import com.jungle.majorhttps.kotlin.request.base.BizDownloadResponse
import com.jungle.majorhttps.kotlin.request.base.BizRequestListener
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream

class BizDownloadRequest : BizBaseRequest<BizDownloadResponse> {

    constructor(
            seqId: Int, method: Int, url: String?,
            params: Map<String, *>, headers: Map<String, String>,
            listener: BizRequestListener<BizDownloadResponse>)
            : super(seqId, method, url, params, headers, listener) {
    }

    override fun parseNetworkResponse(response: NetworkResponse?): Response<BizDownloadResponse> {
        val data = if (response != null && response.data != null) response.data else ByteArray(0)
        return Response.success(
                BizDownloadResponse(response, data),
                HttpHeaderParser.parseCacheHeaders(response))
    }
}


class BizDownloadFileRequest : BizBaseRequest<BizDownloadFileResponse> {

    private var mFilePath: String


    constructor(
            seqId: Int, method: Int, url: String?,
            params: Map<String, *>, headers: Map<String, String>, filePath: String,
            listener: BizRequestListener<BizDownloadFileResponse>)
            : super(seqId, method, url, params, headers, listener) {

        mFilePath = filePath
    }

    override fun parseNetworkResponse(response: NetworkResponse?): Response<BizDownloadFileResponse> {
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

        return Response.success(
                BizDownloadFileResponse(response, mFilePath),
                HttpHeaderParser.parseCacheHeaders(response))
    }
}
