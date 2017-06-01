package com.jungle.majorhttps.kotlin.request.upload

import com.android.volley.AuthFailureError
import com.android.volley.NetworkResponse
import com.android.volley.Response
import com.android.volley.toolbox.HttpHeaderParser
import com.jungle.majorhttps.kotlin.request.base.BizBaseRequest
import com.jungle.majorhttps.kotlin.request.base.BizMultipartResponse
import com.jungle.majorhttps.kotlin.request.base.BizRequestListener
import java.io.ByteArrayOutputStream
import java.io.IOException

class BizMultipartRequest : BizBaseRequest<BizMultipartResponse> {

    companion object {

        private val UPLOAD_CONTENT_TYPE = "multipart/form-data"
        private val UPLOAD_BOUNDARY = "biz-upload-request-"
    }


    private lateinit var mFormItems: List<MultipartFormItem>

    constructor(
            seqId: Int, method: Int, url: String?,
            items: List<MultipartFormItem>, headers: Map<String, String>,
            listener: BizRequestListener<BizMultipartResponse>)
            : super(seqId, method, url, null, headers, listener) {

        mFormItems = items
        setShouldCache(false)
    }

    override fun getBodyContentType()
            = "$UPLOAD_CONTENT_TYPE; boundary=$UPLOAD_BOUNDARY"

    @Throws(AuthFailureError::class)
    override fun getBody(): ByteArray {
        if (mFormItems.isEmpty()) {
            return super.getBody()
        }

        val size = mFormItems
                .map { it.getFormContent() }
                .sumBy { it?.size ?: 0 }

        //
        // Write Format:
        //
        // --boundary
        // Content-Disposition: form-data; name="files0"; filename="item_file_name_0"
        // Content-Type: application/octet-stream
        // Content-Transfer-Encoding: binary
        //
        // item_file_content_0
        //
        // --boundary
        // Content-Disposition: form-data; name="files1"; filename="item_file_name_1"
        // Content-Type: application/octet-stream
        // Content-Transfer-Encoding: binary
        //
        // item_file_content_1
        //
        // --boundary--
        //

        var index = 0
        val stream = ByteArrayOutputStream(size)
        for (item in mFormItems) {
            val content = item.getFormContent() ?: continue

            val buffer = StringBuilder()
            buffer.append("--").append(UPLOAD_BOUNDARY).append("\r\n")
            buffer.append("Content-Disposition: form-data;")
                    .append(" name=\"").append("files").append(index).append("\";")
                    .append(" filename=\"").append(item.getFormName()).append("\"\r\n")

            buffer.append("Content-Type: ").append(item.getMimeType()).append("\r\n")
            buffer.append("Content-Transfer-Encoding: binary\r\n\r\n")

            try {
                stream.write(buffer.toString().toByteArray(charset("utf-8")))
                stream.write(content)
                stream.write("\r\n".toByteArray(charset("utf-8")))
            } catch (e: IOException) {
                e.printStackTrace()
            }

            ++index
        }

        try {
            stream.write(String.format("--%s--\r\n", UPLOAD_BOUNDARY).toByteArray(charset("utf-8")))
        } catch (e: IOException) {
            e.printStackTrace()
        }

        val result = stream.toByteArray()
        try {
            stream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return result
    }

    override fun parseNetworkResponse(response: NetworkResponse): Response<BizMultipartResponse> {
        return Response.success(
                BizMultipartResponse(response, getResponseContent(response)),
                HttpHeaderParser.parseCacheHeaders(response))
    }
}
