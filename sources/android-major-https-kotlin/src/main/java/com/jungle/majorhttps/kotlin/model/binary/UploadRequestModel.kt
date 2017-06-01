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