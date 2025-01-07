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
fun NewHelpRequestScreen(
    onSubmitSuccess: () -> Unit, // Callback to refresh data in ElderMainScreen
    onCancel: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    // State variables for the form
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Create New Help Request", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        TextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = onCancel) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        val userId = auth.currentUser?.uid
                        if (userId == null) {
                            errorMessage = "User not logged in"
                        } else if (title.isBlank() || description.isBlank()) {
                            errorMessage = "Title and description cannot be empty"
                        } else {
                            isLoading = true
                            errorMessage = null
                            val helpRequest = hashMapOf(
                                "title" to title,
                                "description" to description,
                                "createdBy" to userId,
                                "timestamp" to System.currentTimeMillis()
                            )
                            firestore.collection("help_requests")
                                .add(helpRequest)
                                .addOnSuccessListener {
                                    isLoading = false
                                    onSubmitSuccess() // Notify the parent to refresh
                                }
                                .addOnFailureListener { exception ->
                                    errorMessage = exception.localizedMessage ?: "Failed to save request"
                                    isLoading = false
                                }
                        }
                    }
                ) {
                    Text("Submit")
                }
            }
        }

        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }
    }
}