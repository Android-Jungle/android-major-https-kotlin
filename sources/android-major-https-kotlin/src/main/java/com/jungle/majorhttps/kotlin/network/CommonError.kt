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