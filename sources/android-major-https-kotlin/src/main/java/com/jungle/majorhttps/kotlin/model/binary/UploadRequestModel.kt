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

package com.jungle.majorhttps.kotlin.model.binary

import com.jungle.majorhttps.kotlin.model.base.AbstractModel
import com.jungle.majorhttps.kotlin.model.base.BaseModel
import com.jungle.majorhttps.kotlin.model.base.ModelMethod
import com.jungle.majorhttps.kotlin.request.upload.BinaryMultipartFormItem
import com.jungle.majorhttps.kotlin.request.upload.FileUploadFormItem
import com.jungle.majorhttps.kotlin.request.upload.MultipartFormItem
import java.io.File


class UploadRequestModel : BaseModel<UploadRequestModel, UploadRequestModel.Request, String>() {

    companion object {

        fun newModel() = UploadRequestModel()
    }


    class Request : AbstractModel.Request() {

        private val mFormItems = ArrayList<MultipartFormItem>()

        fun addFormItem(item: MultipartFormItem): Request {
            mFormItems.add(item)
            return this
        }

        fun getFormItems(): List<MultipartFormItem> = mFormItems
    }


    init {
        method(ModelMethod.POST)
    }

    fun addFormItem(item: MultipartFormItem): UploadRequestModel {
        mRequest.addFormItem(item)
        return this
    }

    fun addFormItem(filePath: String): UploadRequestModel {
        if (!File(filePath).exists()) {
            return this
        }

        return addFormItem(FileUploadFormItem(filePath))
    }

    fun addFormItem(fileName: String, content: ByteArray?): UploadRequestModel {
        if (content == null || content.isEmpty()) {
            return this
        }

        return addFormItem(BinaryMultipartFormItem(fileName, content))
    }

    override fun createRequest() = UploadRequestModel.Request()

    override fun loadInternal(): Int {
        return getHttpClient().loadUploadModel(mRequest, this)
    }
}