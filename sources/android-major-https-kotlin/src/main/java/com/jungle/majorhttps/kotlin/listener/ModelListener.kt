package com.jungle.majorhttps.kotlin.listener

import com.jungle.majorhttps.kotlin.model.base.AbstractModel
import com.jungle.majorhttps.kotlin.request.base.NetworkResp

interface ModelSuccessListener<in T> {
    fun onSuccess(networkResp: NetworkResp, response: T)
}

interface ModelErrorListener {
    fun onError(errorCode: Int, message: String)
}


interface ModelListener<in T> : ModelSuccessListener<T>, ModelErrorListener


interface ModelLoadLifeListener<in T : AbstractModel<*, *, *>> {
    fun onBeforeLoad(model: T)
}


interface ModelRequestListener<in T> {

    fun onSuccess(seqId: Int, networkResp: NetworkResp, response: T);

    fun onError(seqId: Int, errorCode: Int, message: String)
}


open class ProxyModelListener<in T>(listener: ModelListener<T>) : ModelListener<T> {

    private var mListener: ModelListener<T>? = listener

    override fun onSuccess(networkResp: NetworkResp, response: T) {
        mListener?.onSuccess(networkResp, response)
    }

    override fun onError(errorCode: Int, message: String) {
        mListener?.onError(errorCode, message)
    }
}


class BothProxyModelListener<in T> : ModelListener<T> {

    private var mSuccessListener: ModelSuccessListener<T>? = null
    private var mErrorListener: ModelErrorListener? = null

    constructor(listener: ModelListener<T>?) {
        mSuccessListener = listener
        mErrorListener = listener
    }

    constructor(success: ModelSuccessListener<T>?, error: ModelErrorListener?) {
        mSuccessListener = success
        mErrorListener = error
    }

    override fun onSuccess(networkResp: NetworkResp, response: T) {
        mSuccessListener?.onSuccess(networkResp, response)
    }

    override fun onError(errorCode: Int, message: String) {
        mErrorListener?.onError(errorCode, message)
    }
}
