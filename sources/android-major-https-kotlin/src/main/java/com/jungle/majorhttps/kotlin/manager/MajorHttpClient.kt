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

package com.jungle.majorhttps.kotlin.manager

import com.android.volley.*
import com.jungle.majorhttps.kotlin.listener.ModelRequestListener
import com.jungle.majorhttps.kotlin.model.base.AbstractModel
import com.jungle.majorhttps.kotlin.model.binary.DownloadFileRequestModel
import com.jungle.majorhttps.kotlin.model.binary.UploadRequestModel
import com.jungle.majorhttps.kotlin.network.CommonError
import com.jungle.majorhttps.kotlin.request.base.BizBaseRequest
import com.jungle.majorhttps.kotlin.request.base.BizBaseResponse
import com.jungle.majorhttps.kotlin.request.base.BizRequestListener
import com.jungle.majorhttps.kotlin.request.base.ExtraHeadersFiller
import com.jungle.majorhttps.kotlin.request.binary.BizBinaryRequest
import com.jungle.majorhttps.kotlin.request.download.BizDownloadFileRequest
import com.jungle.majorhttps.kotlin.request.download.BizDownloadRequest
import com.jungle.majorhttps.kotlin.request.queue.RequestQueueFactory
import com.jungle.majorhttps.kotlin.request.text.BizTextRequest
import com.jungle.majorhttps.kotlin.request.upload.BizMultipartRequest
import java.lang.reflect.Type
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger


class MajorHttpClient {

    companion object {

        val DEFAULT_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(20)
        val UPLOAD_TIMEOUT_MS = TimeUnit.SECONDS.toMillis(40)


        private val mInstance by lazy { MajorHttpClient() }

        fun getDefault() = mInstance

        private data class RequestNode(
                val mSeqId: Int, val mVolleyRequest: Request<*>,
                var mResponseType: Type? = null, var mListener: ModelRequestListener<*>?)
    }


    private val mRequestList: MutableMap<Int, RequestNode> = HashMap<Int, RequestNode>()
    private val mSeqIdGenerator = AtomicInteger()
    private var mDefaultRetryPolicy: RetryPolicy? = null
    private var mUploadRetryPolicy: RetryPolicy? = null
    private var mExtraHeadersFiller: ExtraHeadersFiller? = null
    private var mRequestQueue: RequestQueue? = null


    constructor() : this(null) {
    }

    constructor(factory: RequestQueueFactory?)
            : this(DEFAULT_TIMEOUT_MS, UPLOAD_TIMEOUT_MS, factory) {
    }

    constructor(defaultTimeoutMs: Long, uploadTimeoutMs: Long, factory: RequestQueueFactory?) {
        setDefaultTimeoutMilliseconds(defaultTimeoutMs)
        setUploadTimeoutMilliseconds(uploadTimeoutMs)
        setRequestQueueFactory(factory)
    }

    fun onTerminate() {
        mRequestList.clear()
    }

    fun setRequestQueueFactory(factory: RequestQueueFactory?) {
        if (factory != null) {
            mRequestQueue = factory.createRequestQueue()
        }
    }

    fun setUploadTimeoutMilliseconds(millSeconds: Long) {
        mUploadRetryPolicy = DefaultRetryPolicy(millSeconds.toInt(), 1, 1.0f)
    }

    fun setDefaultTimeoutMilliseconds(millSeconds: Long) {
        mDefaultRetryPolicy = DefaultRetryPolicy(millSeconds.toInt(), 1, 1.0f)
    }

    fun setExtraHeadersFiller(filler: ExtraHeadersFiller?) {
        mExtraHeadersFiller = filler
    }

    @Synchronized
    fun loadTextModel(
            request: AbstractModel.Request,
            listener: ModelRequestListener<String>): Int {

        val seqId = nextSeqId()
        request.seqId(seqId)
        val textRequest = BizTextRequest(
                seqId, request.getRequestMethod().toVolleyMethod(),
                request.getUrl(), request.getRequestParams(),
                request.getRequestHeaders(), request.getBody(),
                mBizTextRequestListener)

        addRequestNode(seqId, request, textRequest, listener)
        return seqId
    }

    @Synchronized
    fun loadBinaryModel(
            request: AbstractModel.Request,
            listener: ModelRequestListener<ByteArray>): Int {

        val seqId = nextSeqId()
        request.seqId(seqId)
        val binaryRequest = BizBinaryRequest(
                seqId, request.getRequestMethod().toVolleyMethod(),
                request.getUrl(), request.getRequestParams(),
                request.getRequestHeaders(), request.getBody()!!,
                mBizBinaryRequestListener)

        addRequestNode(seqId, request, binaryRequest, listener)
        return seqId
    }

    @Synchronized
    fun loadUploadModel(
            request: UploadRequestModel.Request,
            listener: ModelRequestListener<String>): Int {

        val seqId = nextSeqId()
        request.seqId(seqId)
        val uploadRequest = BizMultipartRequest(
                seqId, request.getRequestMethod().toVolleyMethod(),
                request.getUrl(), request.getFormItems(),
                request.getRequestHeaders(),
                mBizUploadRequestListener)

        addRequestNode(seqId, request, uploadRequest, listener)
        return seqId
    }

    @Synchronized
    fun loadDownloadModel(
            request: AbstractModel.Request,
            listener: ModelRequestListener<ByteArray>): Int {

        val seqId = nextSeqId()
        request.seqId(seqId)
        val downloadRequest = BizDownloadRequest(
                seqId, request.getRequestMethod().toVolleyMethod(),
                request.getUrl(), request.getRequestParams(),
                request.getRequestHeaders(),
                mBizDownloadRequestListener)

        addRequestNode(seqId, request, downloadRequest, listener)
        return seqId
    }

    @Synchronized
    fun loadDownloadFileModel(
            request: DownloadFileRequestModel.Request,
            listener: ModelRequestListener<String>): Int {

        val seqId = nextSeqId()
        request.seqId(seqId)
        val downloadFileRequest = BizDownloadFileRequest(
                seqId, request.getRequestMethod().toVolleyMethod(),
                request.getUrl(), request.getRequestParams(),
                request.getRequestHeaders(), request.getFilePath(),
                mBizDownloadFileRequestListener)

        addRequestNode(seqId, request, downloadFileRequest, listener)
        return seqId
    }

    private fun nextSeqId(): Int {
        return mSeqIdGenerator.addAndGet(1)
    }

    @Synchronized
    fun sendRequest(request: Request<*>): Int {
        val seqId = nextSeqId()
        addRequestNode(seqId, null, request, null)

        return seqId
    }

    private fun addRequestNode(
            seqId: Int, modelRequest: AbstractModel.Request?,
            request: Request<*>, listener: ModelRequestListener<*>?) {

        if (mRequestQueue == null) {
            listener?.onError(
                    seqId, CommonError.REQUEST_QUEUE_NOT_INITIALIZED,
                    "RequestQueue is null! use **setRequestQueueFactory** to to initialize RequestQueue first!")
            return
        }

        if (request is BizMultipartRequest) {
            request.setRetryPolicy(mUploadRetryPolicy)
        } else {
            request.setRetryPolicy(mDefaultRetryPolicy)
        }

        if (request is BizBaseRequest<*>) {
            if (modelRequest != null && modelRequest.isFillExtraHeader()) {
                request.setExtraHeadersFiller(mExtraHeadersFiller)
            }
        }

        if (listener != null) {
            mRequestList.put(seqId, RequestNode(seqId, request, null, listener))
        }

        mRequestQueue!!.add(request)
    }

    fun cancelBizModel(seqId: Int) {
        val node = mRequestList.remove(seqId)
        if (node != null) {
            node.mVolleyRequest.cancel()
        }
    }

    fun getRequestQueue() = mRequestQueue


    private val mBizTextRequestListener = object : WrappedRequestListener<String>() {

        override fun handleSuccess(
                seqId: Int,
                listener: ModelRequestListener<String>,
                response: BizBaseResponse<String>) {

            listener.onSuccess(seqId, response.mNetworkResp, response.mContent)
        }
    }

    private val mBizBinaryRequestListener = object : WrappedRequestListener<ByteArray>() {

        override fun handleSuccess(
                seqId: Int,
                listener: ModelRequestListener<ByteArray>,
                response: BizBaseResponse<ByteArray>) {

            listener.onSuccess(seqId, response.mNetworkResp, response.mContent)
        }
    }

    private val mBizUploadRequestListener = object : WrappedRequestListener<String>() {

        override fun handleSuccess(
                seqId: Int,
                listener: ModelRequestListener<String>,
                response: BizBaseResponse<String>) {

            listener.onSuccess(seqId, response.mNetworkResp, response.mContent)
        }
    }

    private val mBizDownloadRequestListener = object : WrappedRequestListener<ByteArray>() {

        override fun handleSuccess(
                seqId: Int,
                listener: ModelRequestListener<ByteArray>,
                response: BizBaseResponse<ByteArray>) {

            listener.onSuccess(seqId, response.mNetworkResp, response.mContent)
        }
    }

    private val mBizDownloadFileRequestListener = object : WrappedRequestListener<String>() {

        override fun handleSuccess(
                seqId: Int,
                listener: ModelRequestListener<String>,
                response: BizBaseResponse<String>) {

            listener.onSuccess(seqId, response.mNetworkResp, response.mContent)
        }
    }


    private inner abstract class WrappedRequestListener<T> : BizRequestListener<T> {

        protected abstract fun handleSuccess(
                seqId: Int, listener: ModelRequestListener<T>, response: BizBaseResponse<T>)


        @Suppress("UNCHECKED_CAST")
        override fun onSuccess(seqId: Int, response: BizBaseResponse<T>) {
            synchronized(this@MajorHttpClient) {
                val node = mRequestList.remove(seqId)
                if (node == null || node.mListener == null) {
                    return
                }

                val listener = node.mListener as ModelRequestListener<T>
                handleSuccess(seqId, listener, response)
            }
        }

        override fun onError(seqId: Int, error: VolleyError?) {
            handleError(seqId, error)
        }
    }

    @Synchronized
    private fun handleError(seqId: Int, error: VolleyError?) {
        val node = mRequestList.remove(seqId)
        if (node == null || node.mListener == null) {
            return
        }

        var message: String? = null
        if (error?.networkResponse != null && error.networkResponse.data != null) {
            message = String(error.networkResponse.data)
        } else {
            message = error.toString()
        }

        node.mListener?.onError(seqId, CommonError.fromError(error), message)
    }
}