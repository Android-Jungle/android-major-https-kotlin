package com.jungle.majorhttps.kotlin.demo

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Toast
import com.jungle.majorhttps.kotlin.manager.MajorHttpClient
import com.jungle.majorhttps.kotlin.model.text.TextRequestModel
import com.jungle.majorhttps.kotlin.network.HttpsUtils
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

    private fun showError(errorCode: Int, message: String) {
        val detail = "Error: errorCode = $errorCode, message = $message."
        Log.e("Main", detail)
        Toast.makeText(this, detail, Toast.LENGTH_LONG).show()
    }

    fun onTextModel(view: View) {
        TextRequestModel
                .newModel()
                .url(DEMO_WEB_URL)
                .success { _, response -> TextViewerActivity.start(context, response) }
                .error { errorCode, message -> showError(errorCode, message) }
                .loadWithProgress(this)
    }
}
