package com.movie.binged

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.movie.binged.ui.navigation.AppNavigation
import com.movie.binged.data.room.db.AppDatabase
import com.movie.binged.ui.theme.BingedTheme
import com.movie.binged.data.dataStore
import com.movie.binged.data.repository.UserRepository

class MainActivity : ComponentActivity() {
    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "app_database"
        ).build()
    }
    private val userRepo by lazy {
        UserRepository(
            historyDao = db.historyDao(),
            favoriteDao = db.favoriteDao(),
            dataStore = applicationContext.dataStore
        )
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            try {
                val currentWebViewPackage = android.webkit.WebView.getCurrentWebViewPackage()
                Log.d("WebView", "Provider: ${currentWebViewPackage?.packageName}")
                Log.d("WebView", "Version: ${currentWebViewPackage?.versionName}")
            } catch (e: Exception) {
                Log.e("WebView", "No WebView provider found: ${e.message}")
            }


            BingedTheme {
                val navHostController = rememberNavController()
                AppNavigation(navHostController,userRepo)

            }
        }
    }
}
