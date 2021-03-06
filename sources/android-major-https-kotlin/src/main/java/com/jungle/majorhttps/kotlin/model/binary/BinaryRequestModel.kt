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