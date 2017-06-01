package com.jungle.majorhttps.kotlin.network

import com.android.volley.VolleyError

interface CommonError {

    companion object {

        val SUCCESS = 0
        val FAILED = -4000
        val PARSE_BODY_ERROR = -40001
        val REQUEST_QUEUE_NOT_INITIALIZED = -4002
        val PARSE_JSON_OBJECT_FAILED = -4003
        val PARSE_JSON_ARRAY_FAILED = -4004


        fun fromError(error: VolleyError?): Int {
            if (error != null && error.networkResponse != null) {
                return error.networkResponse.statusCode
            }

            return FAILED
        }
    }
}