package com.jungle.majorhttps.kotlin.model.base

import com.android.volley.Request

enum class ModelMethod {

    GET,
    DELETE,
    POST,
    PUT,
    HEAD,
    OPTIONS,
    TRACE,
    PATCH;


    fun toVolleyMethod(): Int {
        when (this) {
            GET -> return Request.Method.GET
            POST -> return Request.Method.POST
            DELETE -> return Request.Method.DELETE
            PUT -> return Request.Method.PUT
            HEAD -> return Request.Method.HEAD
            OPTIONS -> return Request.Method.OPTIONS
            TRACE -> return Request.Method.TRACE
            PATCH -> return Request.Method.PATCH
        }
    }
}
