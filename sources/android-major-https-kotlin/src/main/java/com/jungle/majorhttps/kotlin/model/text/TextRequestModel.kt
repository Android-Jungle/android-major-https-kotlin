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

package com.jungle.majorhttps.kotlin.model.text

import android.text.TextUtils
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.jungle.majorhttps.kotlin.listener.ModelRequestListener
import com.jungle.majorhttps.kotlin.model.base.AbstractModel
import com.jungle.majorhttps.kotlin.network.CommonError
import com.jungle.majorhttps.kotlin.request.base.NetworkResp

abstract class AbstractTextRequestModel<Impl : AbstractTextRequestModel<Impl, *>, Data>
    : AbstractModel<Impl, AbstractModel.Request, Data>()
        , ModelRequestListener<String> {

    override fun loadInternal() = getHttpClient().loadTextModel(mRequest, this)

    override fun onError(seqId: Int, errorCode: Int, message: String) {
        doError(errorCode, message)
    }
}


class TextRequestModel : AbstractTextRequestModel<TextRequestModel, String>() {

    companion object {
        fun newModel() = TextRequestModel()
    }

    override fun onSuccess(seqId: Int, networkResp: NetworkResp, response: String?) {
        doSuccess(networkResp, response)
    }
}


class JsonObjectRequestModel : AbstractTextRequestModel<JsonObjectRequestModel, JSONObject>() {

    companion object {
        fun newModel() = JsonObjectRequestModel()
    }

    override fun onSuccess(seqId: Int, networkResp: NetworkResp, response: String?) {
        try {
            val json = JSON.parseObject(response)
            doSuccess(networkResp, json)
        } catch (e: Exception) {
            e.printStackTrace()
            doError(CommonError.PARSE_JSON_OBJECT_FAILED, e.message ?: "")
        }
    }
}


class JsonArrayRequestModel : AbstractTextRequestModel<JsonArrayRequestModel, JSONArray>() {

    companion object {
        fun newModel() = JsonArrayRequestModel()
    }

    override fun onSuccess(seqId: Int, networkResp: NetworkResp, response: String?) {
        try {
            val json = JSON.parseArray(response)
            doSuccess(networkResp, json)
        } catch (e: Exception) {
            e.printStackTrace()
            doError(CommonError.PARSE_JSON_ARRAY_FAILED, e.message ?: "")
        }
    }
}


open class JsonRequestModel<T>(val clazz: Class<T>)
    : AbstractTextRequestModel<JsonRequestModel<T>, T>() {

    companion object {
        fun <T> newModel(clazz: Class<T>) = JsonRequestModel<T>(clazz)
    }


    protected var mResponseClazz: Class<T> = clazz

    override fun onSuccess(seqId: Int, networkResp: NetworkResp, response: String?) {
        if (TextUtils.isEmpty(response)) {
            doSuccess(networkResp, mResponseClazz.newInstance())
            return
        }

        try {
            val data = JSON.parseObject(response, mResponseClazz)
            doSuccess(networkResp, data)
        } catch (e: Exception) {
            e.printStackTrace()
            doError(CommonError.PARSE_BODY_ERROR, e.message ?: "")
        }
    }
}