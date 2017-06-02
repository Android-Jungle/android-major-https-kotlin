package com.jungle.majorhttps.kotlin.request.base

import com.android.volley.NetworkResponse

open class BizBaseResponse<T>(resp: NetworkResponse?, content: T?) {

    var mContent = content
    var mNetworkResp = NetworkResp(resp)
}
