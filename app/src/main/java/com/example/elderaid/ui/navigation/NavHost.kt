package com.example.elderaid.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.elderaid.ui.screens.*
import com.example.elderaid.ui.viewmodel.ElderMainViewModel
import com.google.firebase.auth.FirebaseAuth

@Composable
fun AppNavHost(navController: NavHostController, startDestination: String) {
    val auth = FirebaseAuth.getInstance()
    val elderMainViewModel: ElderMainViewModel = androidx.lifecycle.viewmodel.compose.viewModel()

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
                onSignupSuccess = {
                    navController.navigate("login") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }

        // Elder Main Screen
        composable("elderMain") {
            var previousRequests by remember { mutableStateOf(listOf<Map<String, String>>()) }
            var errorMessage by remember { mutableStateOf<String?>(null) }
            var isLoading by remember { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                val userId = auth.currentUser?.uid
                if (userId != null) {
                    elderMainViewModel.fetchPreviousRequests(
                        elderUserId = userId,
                        onSuccess = { requests ->
                            previousRequests = requests // Assign the fetched requests
                            isLoading = false
                        },
                        onFailure = { error ->
                            errorMessage = error
                            isLoading = false
                        }
                    )
                } else {
                    errorMessage = "User not logged in"
                    isLoading = false
                }
            }

            ElderMainScreen(
                previousRequests = previousRequests,
                isLoading = isLoading,
                errorMessage = errorMessage,
                onNewRequestClick = { navController.navigate("newHelpRequest") },
                onViewApplicantsClick = { requestId ->
                    println("View applicants for request ID: $requestId")
                },
                onProfileClick = { navController.navigate("profile") },
                onSOSClick = { navController.navigate("sos") }
            )
        }

        // New Help Request Screen
        composable("newHelpRequest") {
            NewHelpRequestScreen(
                onSubmitSuccess = {
                    navController.popBackStack() // Return to ElderMainScreen
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }

        // Profile Screen
        composable("profile") {
            ProfileScreen(
                onLogout = {
                    auth.signOut()
                    navController.navigate("login") {
                        popUpTo("elderMain") { inclusive = true } // Clear back stack
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        // SOS Screen
        composable("sos") {
            SOSScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
