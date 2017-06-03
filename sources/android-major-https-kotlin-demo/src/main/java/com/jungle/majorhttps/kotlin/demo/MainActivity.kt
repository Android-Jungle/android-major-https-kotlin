/**
 * Android Jungle-Major-Https-Kotlin demo framework project.
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

package com.jungle.majorhttps.kotlin.demo

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.serializer.SerializerFeature
import com.jungle.majorhttps.kotlin.listener.ModelListener
import com.jungle.majorhttps.kotlin.manager.MajorHttpClient
import com.jungle.majorhttps.kotlin.model.binary.DownloadFileRequestModel
import com.jungle.majorhttps.kotlin.model.binary.DownloadRequestModel
import com.jungle.majorhttps.kotlin.model.binary.UploadRequestModel
import com.jungle.majorhttps.kotlin.model.text.JsonRequestModel
import com.jungle.majorhttps.kotlin.model.text.TextRequestModel
import com.jungle.majorhttps.kotlin.network.HttpsUtils
import com.jungle.majorhttps.kotlin.request.base.NetworkResp
import com.jungle.majorhttps.kotlin.request.queue.HttpsRequestQueueFactory

class MainActivity : AppCompatActivity() {

    companion object {

        private val DEMO_WEB_URL = "https://www.zhihu.com"

        private val DEMO_JSON_URL = "https://raw.githubusercontent.com/arnozhang/android-major-https/master/docs/demo.json"

        private val DEMO_UPLOAD_URL = "https://raw.githubusercontent.com/upload_test"
    }


    private val context: Context
        get() = this


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val certs = arrayOf("zhihu.cer", "github.cer", "githubusercontent.cer")
        val domains = arrayOf("zhihu.com", "github.com", "githubusercontent.com")

        val factory = HttpsRequestQueueFactory(this, certs)
        factory.setHostnameVerifier(HttpsUtils.DomainHostnameVerifier(domains))
        MajorHttpClient.getDefault().setRequestQueueFactory(factory)
    }

    private var showError = { errorCode: Int, message: String ->
        val detail = "Error: errorCode = $errorCode, message = $message."
        Log.e("Main", detail)
        Toast.makeText(this, detail, Toast.LENGTH_LONG).show()
    }

    fun onTextModel(view: View) {
        TextRequestModel
                .newModel()
                .url(DEMO_WEB_URL)
                .success { _, response -> TextViewerActivity.start(context, response) }
                .error(showError)
                .loadWithProgress(this)
    }

    fun onJsonModel(view: View) {
        JsonRequestModel
                .newModel(GithubUserInfo::class.java)
                .url(DEMO_JSON_URL)
                .error(showError)
                .load { _, response ->
                    var info = JSON.toJSONString(response, SerializerFeature.PrettyFormat)
                    info = "Load Json object success!\n\n$info"
                    TextViewerActivity.start(context, info)
                }
    }

    fun onDownloadModel(view: View) {
        DownloadRequestModel
                .newModel()
                .url(DEMO_JSON_URL)
                .loadWithProgress(this, object : ModelListener<ByteArray> {
                    override fun onSuccess(networkResp: NetworkResp, response: ByteArray?) {
                        val content = String(response!!)
                        TextViewerActivity.start(context, content)
                    }

                    override fun onError(errorCode: Int, message: String) {
                        showError(errorCode, message)
                    }
                })
    }

    private fun getDemoFilePath(): String {
        return Environment.getExternalStorageDirectory().path + "/demo.json"
    }

    fun onDownloadFileModel(view: View) {
        val file = getDemoFilePath()
        val loadingText = "Downloading File: \n$file"

        DownloadFileRequestModel
                .newModel()
                .url(DEMO_JSON_URL)
                .filePath(file)
                .error(showError)
                .beforeStart {
                    ActivityCompat.requestPermissions(this@MainActivity,
                            arrayOf<String>(Manifest.permission.WRITE_EXTERNAL_STORAGE), 100)
                }
                .loadWithProgress(context, loadingText, { _, _ ->
                    Toast.makeText(context, "Download file SUCCESS! file = $file.",
                            Toast.LENGTH_SHORT).show()
                })
    }

    fun onUploadFileModel(view: View) {
        val file = getDemoFilePath()

        UploadRequestModel
                .newModel()
                .url(DEMO_UPLOAD_URL)
                .addFormItem(file)
                .error(showError)
                .loadWithProgress(this, "Uploading...", { _, _ ->
                    Toast.makeText(context, "Upload file SUCCESS! file = $file.",
                            Toast.LENGTH_SHORT).show()
                })
    }
}
