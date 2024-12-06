package com.example.elderaid.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.elderaid.ui.screens.LoginScreen
import com.example.elderaid.ui.screens.SignupScreen

@Composable
fun AppNavHost(navController: NavHostController, startDestination: String) {
    NavHost(navController = navController, startDestination = startDestination) {
        composable("login") {
            LoginScreen(
                onSignupClick = { navController.navigate("signup") },
                onLoginSuccess = { /* Handle login success */ }
            )
        }
        composable("signup") {
            SignupScreen(
                onSignupSuccess = { navController.navigate("login") }
            )
        }
    }
}
