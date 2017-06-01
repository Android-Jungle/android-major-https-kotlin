package com.jungle.majorhttps.kotlin.manager

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.AnimationDrawable
import android.text.TextUtils
import android.widget.TextView
import com.jungle.majorhttps.kotlin.R
import com.jungle.majorhttps.kotlin.listener.ProxyModelListener
import com.jungle.majorhttps.kotlin.model.base.AbstractModel
import com.jungle.majorhttps.kotlin.request.base.NetworkResp

class MajorProgressLoadManager private constructor() {

    companion object {

        private val mInstance: MajorProgressLoadManager by lazy { MajorProgressLoadManager() }

        fun getInstance() = mInstance

        private data class LoadingInfo(var mSeqId: Int, var mLoadingText: String?)
    }


    interface LoadingDialogCreator {
        fun createDialog(context: Context): Dialog
    }


    private var mLoadingDialog: Dialog? = null
    private var mLoadingDialogCreator: LoadingDialogCreator? = null
    private var mLoadingInfoList: MutableList<LoadingInfo> = ArrayList<LoadingInfo>()


    fun onTerminate() {
        mLoadingInfoList.clear()
        mLoadingDialog?.dismiss()
        mLoadingDialog = null
    }

    fun setLoadingDialogCreator(creator: LoadingDialogCreator) {
        mLoadingDialogCreator = creator
    }

    fun <T> load(context: Context?, model: AbstractModel<*, *, T>, loadingText: String?): Int {
        if (context == null) {
            return model.load()
        }

        val listener = model.getListener()
        val seqId = model.load(object : ProxyModelListener<T>(listener) {

            override fun onSuccess(networkResp: NetworkResp, response: T) {
                hideLoading(model.getSeqId())
                super.onSuccess(networkResp, response)
            }

            override fun onError(errorCode: Int, message: String) {
                hideLoading(model.getSeqId())
                super.onError(errorCode, message)
            }
        })

        showLoading(context, seqId, loadingText)
        return seqId
    }

    @Synchronized
    fun showLoading(context: Context, seqId: Int, loadingText: String?) {
        if (mLoadingDialog == null) {
            mLoadingDialog = createLoadingDialog(context)
        }

        val loadingTextInner = if (!TextUtils.isEmpty(loadingText))
            loadingText else context.getString(R.string.loading_now)

        updateLoadingText(loadingTextInner)
        mLoadingInfoList.add(LoadingInfo(seqId, loadingTextInner))

        if (!mLoadingDialog!!.isShowing) {
            val iconView = mLoadingDialog?.findViewById(R.id.request_loading_icon)
            val drawable = iconView?.background
            if (drawable is AnimationDrawable) {
                drawable.start()
            }

            mLoadingDialog?.show()
        }
    }

    private fun updateLoadingText(loadingText: String?) {
        val loadingTextView = mLoadingDialog?.findViewById(R.id.request_loading_text) as TextView
        loadingTextView.text = loadingText
    }

    private fun createLoadingDialog(context: Context): Dialog? {
        var dialog: Dialog? = mLoadingDialogCreator?.createDialog(context)

        if (dialog == null) {
            dialog = Dialog(context)
            dialog.setContentView(R.layout.dialog_loading_request)
            dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        }

        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }

    @Synchronized
    fun hideLoading(seqId: Int) {
        if (mLoadingDialog == null) {
            return
        }

        val iterator = mLoadingInfoList.iterator()
        while (iterator.hasNext()) {
            val info = iterator.next()
            if (info.mSeqId == seqId) {
                iterator.remove()
                break
            }
        }

        if (mLoadingInfoList.isEmpty()) {
            mLoadingDialog?.dismiss()
            mLoadingDialog = null
        } else {
            val last = mLoadingInfoList.last()
            updateLoadingText(last.mLoadingText)
        }
    }
}