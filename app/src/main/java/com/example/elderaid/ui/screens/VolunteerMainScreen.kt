package com.example.elderaid.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import com.example.elderaid.ui.components.OfferCard
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun VolunteerMainScreen(
    onTaskClick: (Map<String, Any>) -> Unit
) {
    var tasks by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedTask by remember { mutableStateOf<Map<String, Any>?>(null) } // For task details dialog

    // Fetch tasks from Firestore
    LaunchedEffect(Unit) {
        FirebaseFirestore.getInstance().collection("help_requests")
            .get()
            .addOnSuccessListener { querySnapshot ->
                tasks = querySnapshot.documents.mapNotNull { it.data }
                isLoading = false
            }
            .addOnFailureListener { exception ->
                errorMessage = exception.localizedMessage
                isLoading = false
            }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (errorMessage != null) {
            Text(text = "Error: $errorMessage", color = MaterialTheme.colorScheme.error)
        } else {
            LazyColumn {
                items(tasks) { task ->
                    OfferCard(
                        offer = task,
                        onAccept = {
                            // Handle Accept logic
                        },
                        onReject = {
                            // Handle Reject logic
                        },
                        onDetails = {
                            selectedTask = task // Show task details
                        }
                    )
                }
            }
        }

        // Task details dialog
        selectedTask?.let { task ->
            TaskDetailsDialog(
                task = task,
                onDismiss = { selectedTask = null }
            )
        }
    }
}

@Composable
fun TaskDetailsDialog(
    task: Map<String, Any>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        },
        title = {
            Text(text = task["title"] as? String ?: "No Title")
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Date: ${task["date"] ?: "No Date"}")
                Text("Start Time: ${task["startTime"] ?: "No Start Time"}")
                Text("End Time: ${task["endTime"] ?: "No End Time"}")
                Text("Location: ${task["location"] ?: "No Location"}")
                Text("Description: ${task["description"] ?: "No Description"}")
                Text("Category: ${task["category"] ?: "No Category"}")
            }
        }
    )
}
