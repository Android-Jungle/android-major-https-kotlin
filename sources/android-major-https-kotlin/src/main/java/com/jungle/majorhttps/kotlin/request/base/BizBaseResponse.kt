package com.jungle.majorhttps.kotlin.request.base

import com.android.volley.NetworkResponse

open class BizBaseResponse<T>(resp: NetworkResponse?, content: T) {

    var mContent = content
    var mNetworkResp = NetworkResp(resp)
}


/**
 * for text.
 */
class BizTextResponse(resp: NetworkResponse?, content: String)
    : BizBaseResponse<String>(resp, content) {
}


/**
 * for binary data.
 *
 * use POST.
 */
class BizBinaryResponse(resp: NetworkResponse?, content: ByteArray)
    : BizBaseResponse<ByteArray>(resp, content) {
}


/**
 * for download binary data.
 */
class BizDownloadResponse(resp: NetworkResponse?, content: ByteArray)
    : BizBaseResponse<ByteArray>(resp, content) {
}


/**
 * for download file.
 */
class BizDownloadFileResponse(resp: NetworkResponse?, filePath: String)
    : BizBaseResponse<String>(resp, filePath) {
}


/**
 * for multipart file upload.
 */
class BizMultipartResponse(resp: NetworkResponse?, content: String)
    : BizBaseResponse<String>(resp, content) {
}