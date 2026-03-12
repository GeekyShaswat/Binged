package com.movie.binged.ui.screens

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
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

    // ← Check WebView is available before trying to render
    val webViewAvailable = remember {
        try {
            val webViewPackage = android.webkit.WebView.getCurrentWebViewPackage()
            Log.d("WebView", "Provider: ${webViewPackage?.packageName}")
            webViewPackage != null
        } catch (e: Exception) {
            Log.e("WebView", "WebView check failed: ${e.message}")
            false
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {

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

                // ← Opens Play Store to WebView page
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
                            // Play Store not available, open browser instead
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

                // ← Also keep go back option
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
        }else {
            // ← Your existing WebView code unchanged
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
                        settings.userAgentString =
                            "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Mobile Safari/537.36"

                        CookieManager.getInstance().setAcceptCookie(true)
                        CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)

                        val adDomains = listOf(
                            "doubleclick.net", "googlesyndication.com",
                            "adservice.google.com", "googletagmanager.com",
                            "googletagservices.com", "amazon-adsystem.com",
                            "moatads.com", "outbrain.com", "taboola.com",
                            "popads.net", "popcash.net", "adnxs.com",
                            "rubiconproject.com", "openx.net", "pubmatic.com",
                            "criteo.com", "adsafeprotected.com", "scorecardresearch.com",
                            "exoclick.com", "trafficjunky.net", "juicyads.com",
                            "plugrush.com", "hilltopads.net", "propellerads.com",
                            "adsterra.com", "clickadu.com", "zedo.com",
                            "yllix.com", "adf.ly"
                        )

                        val adPatterns = listOf(
                            "/ads/", "/ad/", "/adserver/", "/adserve/",
                            "/banner/", "/popup/", "/popunder/", "/tracking/",
                            "/tracker/", "ad_unit", "adzone", "pop-up", "popunder"
                        )

                        fun shouldBlock(url: String): Boolean {
                            val lowerUrl = url.lowercase()
                            return adDomains.any { lowerUrl.contains(it) } ||
                                    adPatterns.any { lowerUrl.contains(it) }
                        }

                        webViewClient = object : WebViewClient() {
                            override fun shouldOverrideUrlLoading(
                                view: WebView,
                                request: WebResourceRequest
                            ): Boolean {
                                val url = request.url.toString()
                                if (url.startsWith("intent://") || url.startsWith("market://")) return true
                                if (shouldBlock(url)) return true
                                return false
                            }

                            override fun shouldInterceptRequest(
                                view: WebView,
                                request: WebResourceRequest
                            ): WebResourceResponse? {
                                if (shouldBlock(request.url.toString())) {
                                    return WebResourceResponse("text/plain", "utf-8", null)
                                }
                                return super.shouldInterceptRequest(view, request)
                            }
                        }

                        webChromeClient = object : WebChromeClient() {
                            private var customViewCallback: CustomViewCallback? = null

                            override fun onShowCustomView(view: View, callback: CustomViewCallback) {
                                customViewCallback = callback
                                customView = view
                                val decorView = (ctx as? Activity)?.window?.decorView as? ViewGroup
                                decorView?.addView(
                                    view, ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )
                                )
                            }

                            override fun onHideCustomView() {
                                val view = customView ?: return
                                val decorView = (ctx as? Activity)?.window?.decorView as? ViewGroup
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

        // ← Back button always visible regardless of WebView state
        IconButton(
            onClick = { navController.navigateUp() },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
        ) {
            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
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