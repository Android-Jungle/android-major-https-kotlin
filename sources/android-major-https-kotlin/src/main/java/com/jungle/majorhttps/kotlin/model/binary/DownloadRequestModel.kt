package com.jungle.majorhttps.kotlin.model.binary

import com.jungle.majorhttps.kotlin.model.base.AbstractModel
import com.jungle.majorhttps.kotlin.model.base.BaseModel


class DownloadRequestModel : BaseModel<DownloadRequestModel, AbstractModel.Request, ByteArray>() {

    companion object {

        fun newModel() = DownloadRequestModel()
    }


    override fun loadInternal(): Int {
        return getHttpClient().loadDownloadModel(mRequest, this)
    }
}