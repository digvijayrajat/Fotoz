package com.digvijay.fotoz.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebViewClient
import com.digvijay.fotoz.R
import kotlinx.android.synthetic.main.activity_web_view.*


class WebViewActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_view)

        var url = intent.getStringExtra("url")
        webview.webViewClient = WebViewClient()
        webview.webChromeClient = WebChromeClient()
        webview.settings.allowFileAccess = true
        webview.settings.allowContentAccess = true
        webview.settings.allowUniversalAccessFromFileURLs = true
        webview.settings.domStorageEnabled = true
        webview.settings.allowFileAccessFromFileURLs = true
        webview.settings.javaScriptEnabled = true
        webview.settings.loadWithOverviewMode = false
        webview.settings.useWideViewPort = true
        if (url == null || url.isEmpty() || url.isBlank())

            webview.loadUrl(intent.getStringExtra("experience"))
        else
            webview.loadUrl(url)

        back_button.setOnClickListener {

            finish()
        }
    }




}