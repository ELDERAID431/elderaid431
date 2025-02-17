package com.example.elderaid.ui.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.elderaid.ui.screens.*
import com.example.elderaid.ui.viewmodel.ElderMainViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun AppNavHost(navController: NavHostController, startDestination: String) {
    val auth = FirebaseAuth.getInstance()

    NavHost(navController = navController, startDestination = startDestination) {

        // Get Started Screen
        composable("getstarted") {
            GetStartedScreen(navController = navController)
        }

        // Login Screen
        composable("login") {
            LoginScreen(
                onSignupClick = { navController.navigate("signup") },
                onLoginSuccess = {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        FirebaseFirestore.getInstance().collection("users").document(userId)
                            .get()
                            .addOnSuccessListener { document ->
                                val role = document.getString("role")
                                val userName = document.getString("fullName") ?: "User"
                                when (role) {
                                    "Elder" -> navController.navigate("elderMain/$userName") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                    "Volunteer" -> navController.navigate("volunteerMain") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                    else -> println("Unknown role: $role")
                                }
                            }
                            .addOnFailureListener {
                                println("Error fetching user role: ${it.localizedMessage}")
                            }
                    }
                }
            )
        }

        // Signup Screen
        composable("signup") {
            SignupScreen(
                onSignupSuccess = {
                    navController.navigate("login") {
                        popUpTo("signup") { inclusive = true }
                    }
                },
                onBack = {
                    navController.navigate("login") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }

        // Elder Main Screen
        composable("elderMain/{userName}") { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName") ?: "Elder"
            val viewModel: ElderMainViewModel = viewModel()
            var previousRequests by remember { mutableStateOf(listOf<Map<String, Any>>()) }
            var isLoading by remember { mutableStateOf(false) }
            var errorMessage by remember { mutableStateOf<String?>(null) }

            LaunchedEffect(Unit) {
                isLoading = true
                viewModel.fetchPreviousRequests(
                    elderUserId = auth.currentUser?.uid ?: "",
                    onSuccess = { requests ->
                        previousRequests = requests
                        isLoading = false
                    },
                    onFailure = { error ->
                        errorMessage = error
                        isLoading = false
                    }
                )
            }

            ElderMainScreen(
                userName = userName,
                previousRequests = previousRequests,
                isLoading = isLoading,
                errorMessage = errorMessage,
                onNewRequestClick = { navController.navigate("newHelpRequest") },
                onVolunteerOffersClick = { navController.navigate("volunteerOffers") },
                onProfileClick = { navController.navigate("profile") },
                onSOSClick = { navController.navigate("sos") }
            )
        }

        // Volunteer Offers Screen
        composable("volunteerOffers") {
            VolunteerOffersScreen(
                onBack = {
                    navController.popBackStack()
                },
                onProfileClick = {
                    navController.navigate("profile")
                }
            )
        }

        // Volunteer Main Screen
        composable("volunteerMain") {
            VolunteerMainScreen(
                onTaskClick = { task ->
                    println("Clicked on task: $task")
                },
                onProfileClick = {
                    navController.navigate("profile")
                },
                onSOSClick = {
                    navController.navigate("sos")
                }
            )
        }

        // New Help Request Screen
        composable("newHelpRequest") {
            NewHelpRequestScreen(
                onSubmitSuccess = {
                    val userName = auth.currentUser?.displayName ?: "User"
                    navController.navigate("elderMain/$userName") {
                        popUpTo("elderMain/{userName}") { inclusive = true }
                    }
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
                        popUpTo("elderMain") { inclusive = true }
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
