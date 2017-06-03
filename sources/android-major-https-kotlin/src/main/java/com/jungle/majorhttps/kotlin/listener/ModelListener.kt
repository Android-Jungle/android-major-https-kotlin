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

package com.jungle.majorhttps.kotlin.listener

import com.jungle.majorhttps.kotlin.model.base.AbstractModel
import com.jungle.majorhttps.kotlin.request.base.NetworkResp

typealias ModelSuccessListener<T> = (networkResp: NetworkResp, response: T?) -> Unit
typealias ModelErrorListener = (errorCode: Int, message: String) -> Unit
typealias ModelBeforeStartListener<T> = (model: T) -> Unit
typealias ModelRequestFiller<T> = (request: T) -> Unit
typealias ModelBeforeSuccessListener<T, Data> = (model: T, networkResp: NetworkResp, response: Data?) -> Unit


interface ModelListener<in T> {

    fun onSuccess(networkResp: NetworkResp, response: T?)

    fun onError(errorCode: Int, message: String)
}


interface ModelLoadLifeListener<in T : AbstractModel<*, *, *>, in Data> {

    fun onLoadStart(model: T)

    fun onBeforeSuccess(model: T, networkResp: NetworkResp, response: Data?)
}


interface ModelRequestListener<in T> {

    fun onSuccess(seqId: Int, networkResp: NetworkResp, response: T?);

    fun onError(seqId: Int, errorCode: Int, message: String)
}


open class ProxyModelListener<in T> : ModelListener<T> {

    private var mSuccessListener: ModelSuccessListener<T>? = null
    private var mErrorListener: ModelErrorListener? = null

    constructor(listener: ModelListener<T>?) {
        mSuccessListener = {
            networkResp, response ->
            listener?.onSuccess(networkResp, response)
        }

        mErrorListener = {
            errorCode, message ->
            listener?.onError(errorCode, message)
        }
    }

    constructor(success: ModelSuccessListener<T>?, error: ModelErrorListener?) {
        mSuccessListener = success
        mErrorListener = error
    }

    override fun onSuccess(networkResp: NetworkResp, response: T?) {
        mSuccessListener?.invoke(networkResp, response)
    }

    override fun onError(errorCode: Int, message: String) {
        mErrorListener?.invoke(errorCode, message)
    }
}
