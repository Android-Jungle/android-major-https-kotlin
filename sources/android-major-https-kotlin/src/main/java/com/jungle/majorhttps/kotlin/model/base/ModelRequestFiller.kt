package com.jungle.majorhttps.kotlin.model.base

interface ModelRequestFiller<in Req : AbstractModel.Request> {

    fun fill(request: Req);
}
