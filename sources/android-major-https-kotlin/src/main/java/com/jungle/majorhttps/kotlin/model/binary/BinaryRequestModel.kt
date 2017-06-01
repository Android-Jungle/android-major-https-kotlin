package com.jungle.majorhttps.kotlin.model.binary

import com.jungle.majorhttps.kotlin.model.base.AbstractModel
import com.jungle.majorhttps.kotlin.model.base.BaseModel
import com.jungle.majorhttps.kotlin.model.base.ModelMethod

class BinaryRequestModel() : BaseModel<BinaryRequestModel, AbstractModel.Request, ByteArray>() {

    companion object {

        fun newModel() = BinaryRequestModel()
    }


    init {
        method(ModelMethod.POST)
    }

    override fun loadInternal() = getHttpClient().loadBinaryModel(mRequest, this)

}