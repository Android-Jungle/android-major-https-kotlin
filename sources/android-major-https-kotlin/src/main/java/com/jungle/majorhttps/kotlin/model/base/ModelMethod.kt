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
