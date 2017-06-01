package com.jungle.majorhttps.kotlin.model.binary

import com.jungle.majorhttps.kotlin.model.base.AbstractModel
import com.jungle.majorhttps.kotlin.model.base.BaseModel


class DownloadFileRequestModel
    : BaseModel<DownloadFileRequestModel, DownloadFileRequestModel.Request, String>() {


    companion object {

        fun newModel() = DownloadFileRequestModel()
    }


    class Request : AbstractModel.Request() {

        private lateinit var mFilePath: String

        fun filePath(filePath: String): Request {
            mFilePath = filePath
            return this
        }

        fun getFilePath() = mFilePath
    }


    fun filePath(filePath: String): DownloadFileRequestModel {
        mRequest.filePath(filePath)
        return this
    }

    override fun createRequest() = Request()

    override fun loadInternal(): Int {
        return getHttpClient().loadDownloadFileModel(mRequest, this)
    }
}
