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

package com.jungle.majorhttps.kotlin.model.base

import android.content.Context
import android.support.annotation.StringRes
import android.util.Log
import com.jungle.majorhttps.kotlin.listener.*
import com.jungle.majorhttps.kotlin.manager.MajorHttpClient
import com.jungle.majorhttps.kotlin.manager.MajorProgressLoadManager
import com.jungle.majorhttps.kotlin.request.base.NetworkResp

abstract class AbstractModel<Impl : AbstractModel<Impl, *, *>, Req : AbstractModel.Request, Data> {

    companion object {

        val INVALID_SEQ_ID = -1
        val TAG = "AbstractModel"
    }


    open class Request {

        private var mSeqId = INVALID_SEQ_ID
        private var mUrl: String? = null
        private var mRequestMethod = ModelMethod.GET
        private var mRequestParams: MutableMap<String, Any> = HashMap<String, Any>()
        private var mRequestHeaders: MutableMap<String, String> = HashMap<String, String>()
        private var mBody: ByteArray? = null
        private var mFillExtraHeader = false

        fun seqId(seqId: Int): Request {
            mSeqId = seqId
            return this
        }

        fun url(url: String): Request {
            mUrl = url
            return this
        }

        fun method(method: ModelMethod): Request {
            mRequestMethod = method
            return this
        }

        fun head(key: String, value: String): Request {
            mRequestHeaders.put(key, value)
            return this
        }

        fun param(key: String, obj: Any): Request {
            mRequestParams.put(key, obj)
            return this
        }

        fun paramCheckNull(key: String, obj: Any?): Request {
            if (obj != null) {
                mRequestParams.put(key, obj)
            }

            return this
        }

        fun addParams(params: Map<String, Any>): Request {
            mRequestParams.putAll(params)
            return this
        }

        fun fillExtraHeader(fill: Boolean): Request {
            mFillExtraHeader = fill
            return this
        }

        fun body(body: ByteArray): Request {
            mBody = body
            return this
        }

        fun getSeqId() = mSeqId

        fun getUrl() = mUrl

        fun getRequestMethod() = mRequestMethod

        fun getRequestParams() = mRequestParams

        fun getRequestHeaders() = mRequestHeaders

        fun isFillExtraHeader() = mFillExtraHeader

        fun getBody() = mBody
    }


    protected var mRequest: Req = createRequest()
    protected var mErrorListener: ModelErrorListener? = null
    protected var mSuccessListener: ModelSuccessListener<Data>? = null
    protected var mBeforeStartListener: ModelBeforeStartListener<Impl>? = null
    protected var mBeforeSuccessListener: ModelBeforeSuccessListener<Impl, Data>? = null
    protected var mModelFiller: ModelRequestFiller<Req>? = null
    protected var mHttpClient: MajorHttpClient? = null


    protected open fun createRequest(): Req {
        return Request() as Req
    }

    @Suppress("UNCHECKED_CAST")
    protected fun asImpl(): Impl {
        return asImpl()
    }

    fun url(url: String): Impl {
        mRequest.url(url)
        return asImpl()
    }

    fun method(method: ModelMethod): Impl {
        mRequest.method(method)
        return asImpl()
    }

    fun head(key: String, value: String): Impl {
        mRequest.head(key, value)
        return asImpl()
    }

    fun param(key: String, obj: Any): Impl {
        mRequest.param(key, obj)
        return asImpl()
    }

    fun paramCheckNull(key: String, obj: Any?): Impl {
        mRequest.paramCheckNull(key, obj)
        return asImpl()
    }

    fun addParams(params: Map<String, Any>): Impl {
        mRequest.addParams(params)
        return asImpl()
    }

    fun filler(filler: ModelRequestFiller<Req>): Impl {
        mModelFiller = filler
        return asImpl()
    }

    fun listener(listener: ModelListener<Data>): Impl {
        mSuccessListener = {
            networkResp, response ->
            listener.onSuccess(networkResp, response)
        }

        mErrorListener = {
            errorCode, message ->
            listener.onError(errorCode, message)
        }

        return asImpl()
    }

    fun success(listener: ModelSuccessListener<Data>): Impl {
        mSuccessListener = listener
        return asImpl()
    }

    fun error(listener: ModelErrorListener): Impl {
        mErrorListener = listener
        return asImpl()
    }

    fun lifeListener(listener: ModelLoadLifeListener<Impl, Data>): Impl {
        mBeforeStartListener = { listener.onLoadStart(it) }
        mBeforeSuccessListener = { model, networkResponse, response ->
            listener.onBeforeSuccess(model, networkResponse, response)
        }

        return asImpl()
    }

    fun beforeStart(listener: ModelBeforeStartListener<Impl>): Impl {
        mBeforeStartListener = listener
        return asImpl()
    }

    fun beforeSuccess(listener: ModelBeforeSuccessListener<Impl, Data>): Impl {
        mBeforeSuccessListener = listener
        return asImpl()
    }

    fun fillExtraHeader(fill: Boolean): Impl {
        mRequest.fillExtraHeader(fill)
        return asImpl()
    }

    fun client(client: MajorHttpClient): Impl {
        mHttpClient = client
        return asImpl()
    }

    fun load(): Int {
        mBeforeStartListener?.invoke(asImpl())
        mModelFiller?.invoke(mRequest)

        return loadInternal()
    }

    abstract fun loadInternal(): Int

    fun load(listener: ModelListener<Data>): Int {
        return listener(listener).load()
    }

    fun load(listener: ModelSuccessListener<Data>): Int {
        return success(listener).load()
    }

    fun loadWithProgress(context: Context?): Int {
        return MajorProgressLoadManager.getInstance().load(context, this, null)
    }

    fun loadWithProgress(context: Context?, listener: ModelListener<Data>): Int {
        listener(listener)
        return loadWithProgress(context)
    }

    fun loadWithProgress(context: Context?, listener: ModelSuccessListener<Data>): Int {
        success(listener)
        return loadWithProgress(context)
    }

    fun loadWithProgress(context: Context?, loadingText: String): Int {
        return MajorProgressLoadManager.getInstance().load(context, this, loadingText)
    }

    fun loadWithProgress(context: Context?, loadingText: String, listener: ModelListener<Data>): Int {
        listener(listener)
        return loadWithProgress(context, loadingText)
    }

    fun loadWithProgress(context: Context?, loadingText: String, listener: ModelSuccessListener<Data>): Int {
        success(listener)
        return loadWithProgress(context, loadingText)
    }

    fun loadWithProgress(context: Context?, @StringRes loadingText: Int): Int {
        if (context == null) {
            return load()
        }

        return loadWithProgress(context, context.getString(loadingText))
    }

    fun loadWithProgress(context: Context?, @StringRes loadingText: Int, listener: ModelListener<Data>): Int {
        listener(listener)
        return loadWithProgress(context, loadingText)
    }

    fun loadWithProgress(context: Context?, @StringRes loadingText: Int, listener: ModelSuccessListener<Data>): Int {
        success(listener)
        return loadWithProgress(context, loadingText)
    }

    fun cancel() {
        getHttpClient().cancelBizModel(mRequest.getSeqId())
    }

    fun getSeqId() = mRequest.getSeqId()

    fun getSuccessListener() = mSuccessListener

    fun getErrorListener() = mErrorListener

    fun getListener() = ProxyModelListener(mSuccessListener, mErrorListener)

    protected fun doSuccess(networkResp: NetworkResp, response: Data?) {
        mBeforeSuccessListener?.invoke(asImpl(), networkResp, response)
        mSuccessListener?.invoke(networkResp, response)
    }

    protected fun doError(errorCode: Int, message: String) {
        Log.e(TAG, "doError! errorCode=$errorCode, url=${mRequest.getUrl()}, message=[ $message ]")
        mErrorListener?.invoke(errorCode, message)
    }

    protected fun getHttpClient(): MajorHttpClient {
        return if (mHttpClient != null) mHttpClient!! else MajorHttpClient.getDefault()
    }
}
