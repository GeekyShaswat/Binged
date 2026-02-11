package com.movie.binged

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.movie.binged.navigation.AppNavigation
import com.movie.binged.ui.theme.BingedTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BingedTheme {
                val navHostController = rememberNavController()
                AppNavigation(navHostController)

            }
        }
    }
}
