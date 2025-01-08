package com.example.elderaid.ui.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.elderaid.ui.screens.*
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
                                if (role == "Elder") {
                                    navController.navigate("elderMain")
                                } else if (role == "Volunteer") {
                                    navController.navigate("volunteerMain")
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
                }
            )
        }

        // Elder Main Screen
        composable("elderMain") {
            ElderMainScreen(
                previousRequests = listOf(),
                isLoading = false,
                errorMessage = null,
                onNewRequestClick = { navController.navigate("newHelpRequest") },
                onViewApplicantsClick = { requestId ->
                    println("View applicants for request ID: $requestId")
                },
                onProfileClick = { navController.navigate("profile") },
                onSOSClick = { navController.navigate("sos") }
            )
        }

        // Volunteer Main Screen
        composable("volunteerMain") {
            var tasks by remember { mutableStateOf(listOf<Map<String, String>>()) }
            var errorMessage by remember { mutableStateOf<String?>(null) }
            var isLoading by remember { mutableStateOf(true) }

            LaunchedEffect(Unit) {
                FirebaseFirestore.getInstance().collection("help_requests")
                    .get()
                    .addOnSuccessListener { documents ->
                        tasks = documents.map { document ->
                            mapOf(
                                "title" to (document.getString("title") ?: "No Title"),
                                "description" to (document.getString("description") ?: "No Description"),
                                "createdBy" to (document.getString("createdBy") ?: "Unknown"),
                                "timestamp" to (document.getTimestamp("timestamp")?.toDate()?.toString() ?: "Unknown")
                            )
                        }
                        isLoading = false
                    }
                    .addOnFailureListener { exception ->
                        errorMessage = exception.localizedMessage ?: "Failed to load tasks"
                        isLoading = false
                    }
            }

            VolunteerMainScreen(
                tasks = tasks,
                isLoading = isLoading,
                errorMessage = errorMessage,
                onTaskClick = { task ->
                    println("Clicked on task: $task")
                }
            )
        }

        // New Help Request Screen
        composable("newHelpRequest") {
            NewHelpRequestScreen(
                onSubmitSuccess = {
                    navController.popBackStack()
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
