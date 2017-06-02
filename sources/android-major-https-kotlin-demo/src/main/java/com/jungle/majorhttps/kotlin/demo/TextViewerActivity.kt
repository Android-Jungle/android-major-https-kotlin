package com.jungle.majorhttps.kotlin.demo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v7.app.AppCompatActivity
import android.widget.TextView


class TextViewerActivity : AppCompatActivity() {

    companion object {

        private val EXTRA_TEXT = "extra_text"

        fun start(context: Context, text: String?) {
            val intent = Intent(context, TextViewerActivity::class.java)
            intent.putExtra(EXTRA_TEXT, text)
            context.startActivity(intent)
        }
    }


    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTitle(R.string.title_text_viewer)
        setContentView(R.layout.activity_text_viewer)

        val textView = findViewById(R.id.text_content) as TextView
        textView.text = intent.getStringExtra(EXTRA_TEXT)
    }
}
