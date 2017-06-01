package com.jungle.majorhttps.kotlin.request.base

interface ExtraHeadersFiller {

    fun fillHeaders(headers: Map<String, String>)
}