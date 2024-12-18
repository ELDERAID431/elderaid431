package com.example.elderaid.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.elderaid.ui.screens.GetStartedScreen
import com.example.elderaid.ui.screens.LoginScreen
import com.example.elderaid.ui.screens.SignupScreen
import com.example.elderaid.ui.screens.ElderMainScreen
import com.example.elderaid.ui.viewmodel.ElderMainViewModel

@Composable
fun AppNavHost(navController: NavHostController, startDestination: String) {
    NavHost(navController = navController, startDestination = startDestination) {
        // Get Started Screen
        composable("getstarted") {
            GetStartedScreen(navController = navController)
        }

        // Login Screen
        composable("login") {
            LoginScreen(
                onSignupClick = { navController.navigate("signup") },
                onLoginSuccess = { navController.navigate("elderMain") }
            )
        }

        // Signup Screen
        composable("signup") {
            SignupScreen(
                onSignupSuccess = { navController.navigate("login") }
            )
        }

        // Elder Main Screen
        composable("elderMain") {
            val elderMainViewModel: ElderMainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
            var previousRequests by remember { mutableStateOf(listOf<String>()) }
            var errorMessage by remember { mutableStateOf<String?>(null) }

            // Fetch previous help requests
            LaunchedEffect(Unit) {
                elderMainViewModel.fetchPreviousRequests(
                    elderUserId = "elder_user_id", // Replace with dynamic user ID
                    onSuccess = { requests -> previousRequests = requests },
                    onFailure = { error -> errorMessage = error }
                )
            }

            ElderMainScreen(
                previousRequests = previousRequests,
                onNewRequestClick = { navController.navigate("newHelpRequest") },
                onViewApplicantsClick = { requestTitle ->
                    println("View applicants for: $requestTitle")
                },
                onProfileClick = { navController.navigate("profile") },
                onSOSClick = { navController.navigate("sos") }
            )

            // Optional: Display error message
            errorMessage?.let { error ->
                println("Error loading requests: $error")
            }
        }

        // New Help Request Screen (placeholder)
        composable("newHelpRequest") {
            println("Navigate to New Help Request Screen")
            // Add screen implementation here
        }

        // Profile Screen (placeholder)
        composable("profile") {
            println("Navigate to Profile Screen")
            // Add screen implementation here
        }

        // SOS Screen (placeholder)
        composable("sos") {
            println("Navigate to SOS Screen")
            // Add screen implementation here
        }
    }
}
