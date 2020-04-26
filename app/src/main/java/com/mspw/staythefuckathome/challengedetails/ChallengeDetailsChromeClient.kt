package com.mspw.staythefuckathome.challengedetails

import android.content.Context
import android.content.pm.ActivityInfo
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.mspw.staythefuckathome.R

class ChallengeDetailsChromeClient(
    private val activity: ChallengeDetailsActivity
) : WebChromeClient() {

    private var customView: View? = null
    private var container: ChallengeDetailsYoutubeViewContainer? = null
    private var customViewCallback: CustomViewCallback? = null

    override fun onProgressChanged(view: WebView?, newProgress: Int) {
        super.onProgressChanged(view, newProgress)
    }

    override fun onShowCustomView(view: View?, callback: CustomViewCallback?) {
        super.onShowCustomView(view, callback)
        if (customView == null) {
            callback?.onCustomViewHidden()
            return
        }

        val windowParams = activity.window.attributes
        val decor = activity.window.decorView as FrameLayout
        container = ChallengeDetailsYoutubeViewContainer(activity).apply {
            addView(view, ViewGroup.LayoutParams.MATCH_PARENT)
        }
        decor.addView(container, ViewGroup.LayoutParams.MATCH_PARENT)
        customView = view
        customViewCallback = callback
        activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        activity.window.attributes = windowParams.apply {
            flags = windowParams.flags or WindowManager.LayoutParams.FLAG_FULLSCREEN
        }
    }

    override fun onHideCustomView() {
        super.onHideCustomView()
        customView?.also {
            val windowParams = activity.window.attributes
            val decor = activity.window.decorView as FrameLayout
            decor.removeView(container)
            it.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
            container = null
            customView = null
            customViewCallback?.onCustomViewHidden()
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            activity.window.attributes = windowParams.apply {
                flags = windowParams.flags and WindowManager.LayoutParams.FLAG_FULLSCREEN.inv()
            }
        }
    }

    private class ChallengeDetailsYoutubeViewContainer @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
    ) : FrameLayout(context, attrs, defStyleAttr) {

        init {
            setBackgroundColor(ContextCompat.getColor(context, R.color.white))
        }

        override fun onTouchEvent(event: MotionEvent?): Boolean {
            return super.onTouchEvent(event)
        }

    }

}