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

package com.jungle.majorhttps.kotlin.request.upload

import android.text.TextUtils
import java.io.File
import java.io.FileInputStream

interface MultipartFormItem {

    companion object {

        val OCTET_MIME_TYPE = "application/octet-stream"
    }

    fun getFormName(): String

    fun getFormContent(): ByteArray?

    fun getMimeType(): String
}


open class BinaryMultipartFormItem : MultipartFormItem {

    protected lateinit var mFormName: String
    protected lateinit var mFormContent: ByteArray
    protected var mMimeType = MultipartFormItem.OCTET_MIME_TYPE


    constructor() {}

    constructor(formName: String, content: ByteArray) : this(formName, null, content) {}

    constructor(formName: String, mimeType: String?, content: ByteArray) {
        mFormName = formName
        mFormContent = content
        mMimeType = mimeType ?: MultipartFormItem.OCTET_MIME_TYPE
    }

    override fun getFormName(): String {
        return mFormName
    }

    override fun getFormContent(): ByteArray {
        return mFormContent
    }

    override fun getMimeType(): String {
        return mMimeType
    }
}


class FileUploadFormItem : BinaryMultipartFormItem {

    private lateinit var mFilePath: String


    constructor() {}

    constructor(filePath: String) : this(filePath, null, ByteArray(0)) {}

    constructor(filePath: String, content: ByteArray) : this(filePath, null, content) {}

    constructor(filePath: String, mimeType: String?, content: ByteArray)
            : super(File(filePath).name, mimeType, content) {

        mFilePath = filePath
    }

    override fun getFormContent(): ByteArray {
        mFormContent = getFileContent(mFilePath)
        return mFormContent
    }

    override fun getMimeType(): String {
        return mMimeType
    }

    companion object {

        fun getFileContent(filePath: String): ByteArray {
            if (TextUtils.isEmpty(filePath)) {
                return ByteArray(0)
            }

            var stream: FileInputStream? = null
            try {
                stream = FileInputStream(filePath)
                val content = ByteArray(stream.available())
                stream.read(content)
                return content
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                if (stream != null) {
                    try {
                        stream.close()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                }
            }

            return ByteArray(0)
        }
    }
}
