package com.jungle.majorhttps.kotlin.request.base

import com.android.volley.VolleyError

interface BizRequestListener<T> {

    fun onSuccess(seqId: Int, response: BizBaseResponse<T>)

    fun onError(seqId: Int, error: VolleyError?)
}
