package com.jungle.majorhttps.kotlin.request.queue

import com.android.volley.RequestQueue

interface RequestQueueFactory {

    fun createRequestQueue(): RequestQueue
}