package com.example.elderaid.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    onBack: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    // State variables to store user information
    var fullName by remember { mutableStateOf("Loading...") }
    var email by remember { mutableStateOf("Loading...") }
    var phoneNumber by remember { mutableStateOf("Loading...") }
    var age by remember { mutableStateOf("Loading...") }
    var location by remember { mutableStateOf("Loading...") }
    var role by remember { mutableStateOf("Loading...") }

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Fetch user information
    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        fullName = document.getString("fullName") ?: "Unknown"
                        email = document.getString("email") ?: "Unknown"
                        phoneNumber = document.getString("phoneNumber") ?: "Unknown"
                        age = document.getString("age") ?: "Unknown"
                        location = document.getString("location") ?: "Unknown"
                        role = document.getString("role") ?: "Unknown"
                        isLoading = false
                    } else {
                        errorMessage = "User data not found"
                        isLoading = false
                    }
                }
                .addOnFailureListener { exception ->
                    errorMessage = exception.localizedMessage ?: "Error fetching data"
                    isLoading = false
                }
        } else {
            errorMessage = "User not logged in"
            isLoading = false
        }
    }

    // UI Layout
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isLoading) {
            CircularProgressIndicator()
        } else if (errorMessage != null) {
            Text("Error: $errorMessage", color = MaterialTheme.colorScheme.error)
        } else {
            Text("User Profile", style = MaterialTheme.typography.headlineMedium)

            Spacer(modifier = Modifier.height(16.dp))

            // Display user information
            Text("Full Name: $fullName")
            Text("Email: $email")
            Text("Phone Number: $phoneNumber")
            Text("Age: $age")
            Text("Location: $location")
            Text("Role: $role")

            Spacer(modifier = Modifier.height(32.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onBack) {
                    Text("Back")
                }
                Button(onClick = {
                    auth.signOut()
                    onLogout()
                }) {
                    Text("Logout")
                }
            }
        }
    }
}