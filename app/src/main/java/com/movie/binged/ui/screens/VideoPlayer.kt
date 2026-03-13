package com.movie.binged.ui.screens

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.navigation.NavController

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun VideoPlayer(url: String?, navController: NavController) {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

    val context = LocalContext.current
    val webViewState = rememberWebViewState()
    var customView by remember { mutableStateOf<View?>(null) }

    val webViewAvailable = remember {
        try {
            val webViewPackage = WebView.getCurrentWebViewPackage()
            Log.d("WebView", "Provider: ${webViewPackage?.packageName}")
            Log.d("WebView", "Version: ${webViewPackage?.versionName}")
            webViewPackage != null
        } catch (e: Exception) {
            Log.e("WebView", "WebView check failed: ${e.message}")
            false
        }
    }

    val allowedDomains = listOf(
        "vidking.net",
        "videasy.net",
        "zupcloud.com",
        "myflixer.to",
        // ← Video CDN domains found in logs
        "workers.dev",              // Cloudflare Workers (tylercampbell1991.workers.dev)
        "tigerflare10.xyz",         // Video segment CDN
        "tylercampbell1991.workers.dev",
        // Common video CDN/streaming domains
        "akamaized.net",
        "akamaihd.net",
        "fastly.net",
        "cloudflare.com",
        "jwplatform.com",
        "jwpcdn.com",
        "bitmovin.com",
        "jsdelivr.net",
        "hlsjs.video-dev.org",
        // HLS/DASH streaming file types
        "m3u8",
        ".mp4",
        ".webm",
        ".ts",
        ".jpg",                     // ← video poster/thumbnail images
        ".png",
        ".jpeg"
    )
    fun isAllowed(url: String): Boolean {
        val lower = url.lowercase()
        return allowedDomains.any { lower.contains(it) }
    }

    val killAdsJs = """
        if ('serviceWorker' in navigator) {
            navigator.serviceWorker.getRegistrations().then(function(registrations) {
                for (let reg of registrations) {
                    reg.unregister();
                    console.log('Unregistered SW:', reg.scope);
                }
            });
        }
        Object.defineProperty(navigator, 'serviceWorker', {
            get: function() { return undefined; }
        });
        window.open = function() { return null; };
        window.alert = function() {};
        window.confirm = function() { return false; };
        window.prompt = function() { return null; };
    """.trimIndent()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        if (!webViewAvailable) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "WebView Not Available",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Android System WebView is required to play content. Please update or enable it.",
                    color = Color.White.copy(alpha = 0.6f),
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 32.dp, vertical = 8.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = {
                        try {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    "market://details?id=com.google.android.webview".toUri()
                                )
                            )
                        } catch (e: Exception) {
                            context.startActivity(
                                Intent(
                                    Intent.ACTION_VIEW,
                                    "https://play.google.com/store/apps/details?id=com.google.android.webview".toUri()
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Update WebView")
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = { navController.navigateUp() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 48.dp),
                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
                ) {
                    Text("Go Back", color = Color.White)
                }
            }
        } else {
            AndroidView(
                factory = { ctx ->
                    WebView(ctx).apply {
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.mediaPlaybackRequiresUserGesture = false
                        settings.loadsImagesAutomatically = true
                        settings.cacheMode = WebSettings.LOAD_DEFAULT
                        settings.useWideViewPort = true
                        settings.loadWithOverviewMode = true
                        settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
                        settings.setGeolocationEnabled(false)
                        settings.javaScriptCanOpenWindowsAutomatically = false
                        settings.setSupportMultipleWindows(false)
                        settings.userAgentString =
                            "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 " +
                                    "(KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"

                        CookieManager.getInstance().setAcceptCookie(true)
                        CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

                        webViewClient = object : WebViewClient() {

                            override fun onPageStarted(
                                view: WebView,
                                url: String,
                                favicon: Bitmap?
                            ) {
                                super.onPageStarted(view, url, favicon)
                                view.evaluateJavascript(killAdsJs, null)
                            }

                            override fun onPageFinished(view: WebView, url: String) {
                                super.onPageFinished(view, url)
                                view.evaluateJavascript(killAdsJs, null)
                            }

                            override fun shouldOverrideUrlLoading(
                                view: WebView,
                                request: WebResourceRequest
                            ): Boolean {
                                val reqUrl = request.url.toString()
                                if (reqUrl.startsWith("intent://") ||
                                    reqUrl.startsWith("market://")) return true
                                return !isAllowed(reqUrl)
                            }

                            override fun shouldInterceptRequest(
                                view: WebView,
                                request: WebResourceRequest
                            ): WebResourceResponse? {
                                val reqUrl = request.url.toString().lowercase()

                                // Always allow vidking's own assets
                                if (reqUrl.contains("vidking.net")) return null

                                // Block everything not in allowlist
                                if (!isAllowed(reqUrl)) {
                                    Log.d("WebView", "BLOCKED: $reqUrl")
                                    return WebResourceResponse(
                                        "text/plain", "utf-8",
                                        "".byteInputStream()
                                    )
                                }

                                Log.d("WebView", "ALLOWED: $reqUrl")
                                return null
                            }
                        }

                        // ── Single webChromeClient with all overrides ────
                        webChromeClient = object : WebChromeClient() {
                            private var customViewCallback: CustomViewCallback? = null

                            // Block pop-up windows
                            override fun onCreateWindow(
                                view: WebView,
                                isDialog: Boolean,
                                isUserGesture: Boolean,
                                resultMsg: android.os.Message?
                            ): Boolean {
                                val href = view.handler.obtainMessage()
                                view.requestFocusNodeHref(href)
                                val popupUrl = href.data?.getString("url") ?: ""
                                Log.d("WebView", "Pop-up blocked: $popupUrl")
                                return false
                            }

                            // Handle fullscreen video
                            override fun onShowCustomView(
                                view: View,
                                callback: CustomViewCallback
                            ) {
                                customViewCallback = callback
                                customView = view
                                val decorView =
                                    (ctx as? Activity)?.window?.decorView as? ViewGroup
                                decorView?.addView(
                                    view,
                                    ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )
                                )
                            }

                            override fun onHideCustomView() {
                                val view = customView ?: return
                                val decorView =
                                    (ctx as? Activity)?.window?.decorView as? ViewGroup
                                decorView?.removeView(view)
                                customView = null
                                customViewCallback?.onCustomViewHidden()
                                customViewCallback = null
                            }
                        }

                        if (webViewState.isEmpty) {
                            url?.let { loadUrl(it) }
                        } else {
                            restoreState(webViewState)
                        }
                    }
                },
                update = { webView -> webView.saveState(webViewState) },
                modifier = Modifier.fillMaxSize()
            )
        }

        // ── Back button always visible ───────────────────────────────────
        IconButton(
            onClick = { navController.navigateUp() },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = Color.White
            )
        }
    }
}

@Composable
fun rememberWebViewState(): Bundle {
    return rememberSaveable { Bundle() }
}

@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    val activity = context as Activity

    DisposableEffect(Unit) {
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            activity.requestedOrientation = originalOrientation
        }
    }
}