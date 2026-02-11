package com.movie.binged.screens


import android.app.Activity
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController

@Composable
fun VideoPlayer(url: String?, navController: NavController) {
    LockScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE)

    val webViewState = rememberWebViewState()
    Box(
      modifier = Modifier.fillMaxSize(),
    ){

        AndroidView(
            factory = { context ->
                WebView(context).apply {

                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    settings.mediaPlaybackRequiresUserGesture = false
                    settings.loadsImagesAutomatically = true

                    settings.cacheMode = WebSettings.LOAD_DEFAULT
                    settings.useWideViewPort = true
                    settings.loadWithOverviewMode = true

                    settings.userAgentString =
                        "Mozilla/5.0 (Linux; Android 13) AppleWebKit/537.36 Chrome/120 Safari/537.36"

                    settings.allowFileAccess = false
                    settings.allowContentAccess = false

                    webViewClient = object : WebViewClient() {

                        override fun shouldOverrideUrlLoading(
                            view: WebView,
                            request: WebResourceRequest
                        ): Boolean {
                            return !request.url.toString().contains("vidking.net")
                        }
                    }

                    webChromeClient = WebChromeClient()

                    if (webViewState.isEmpty) {
                        loadUrl(url!!)
                    } else {
                        restoreState(webViewState)
                    }
                }
            },
            update = { webView ->
                webView.saveState(webViewState)
            },
            modifier = Modifier.fillMaxSize()
        )

        IconButton(
            onClick = { navController.navigateUp() },
            modifier = Modifier
                .padding(16.dp)
                .background(
                    Color.Black.copy(alpha = 0.5f),
                    CircleShape
                )
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
    return rememberSaveable {
        Bundle()
    }
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


