package com.jungle.majorhttps.kotlin.model.base

import com.jungle.majorhttps.kotlin.listener.ModelRequestListener
import com.jungle.majorhttps.kotlin.request.base.NetworkResp

abstract class BaseModel<Impl : BaseModel<Impl, *, *>, Req : AbstractModel.Request, Data>
    : AbstractModel<Impl, Req, Data>(), ModelRequestListener<Data> {

    override fun onSuccess(seqId: Int, networkResp: NetworkResp, response: Data) {
        doSuccess(networkResp, response!!)
    }

    override fun onError(seqId: Int, errorCode: Int, message: String) {
        doError(errorCode, message)
    }
}