package com.example.elderaid

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.elderaid.ui.navigation.AppNavHost
import com.example.elderaid.ui.theme.ElderAidTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ElderAidTheme { // Tema fonksiyonu
                val navController = rememberNavController()
                AppNavHost(navController = navController, startDestination = "login")
            }
        }
    }
}
