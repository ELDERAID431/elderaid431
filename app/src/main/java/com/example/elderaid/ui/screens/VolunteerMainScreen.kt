package com.example.elderaid.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color

@Composable
fun VolunteerMainScreen(
    onTaskClick: (Map<String, Any>) -> Unit
) {
    var tasks by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedTask by remember { mutableStateOf<Map<String, Any>?>(null) } // Track selected task for the dialog

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
                    VolunteerTaskCard(
                        task = task,
                        onTaskClick = { taskDetails ->
                            selectedTask = taskDetails // Open dialog with task details
                        },
                        onAccept = {
                            // Handle acceptance logic here
                            println("Task accepted: ${task["id"]}")
                        },
                        onReject = {
                            // Handle rejection logic here
                            println("Task rejected: ${task["id"]}")
                        }
                    )
                }
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
                    Text(text = "Date: ${task["date"] as? String ?: "No Date"}")
                    Text(text = "Start Time: ${task["startTime"] as? String ?: "No Start Time"}")
                    Text(text = "End Time: ${task["endTime"] as? String ?: "No End Time"}")
                    Text(text = "Location: ${task["location"] as? String ?: "No Location"}")
                    Text(text = "Description: ${task["description"] as? String ?: "No Description"}")
                    Text(text = "Category: ${task["category"] as? String ?: "No Category"}")
                }
            }
        )
    }

    // Dialog to show task details
    selectedTask?.let { task ->
        TaskDetailsDialog(
            task = task,
            onDismiss = { selectedTask = null } // Close the dialog
        )
    }
}


@Composable
fun VolunteerTaskCard(
    task: Map<String, Any>,
    onTaskClick: (Map<String, Any>) -> Unit,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    var isRejected by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val title = task["title"] as? String ?: "No Title"
            val description = task["description"] as? String ?: "No Description"

            Text(text = title)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = description)

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { onTaskClick(task) } // Show task details
                ) {
                    Text("Details")
                }

                if (!isRejected) {
                    Button(onClick = onAccept) {
                        Text("Accept")
                    }
                }

                Button(onClick = {
                    isRejected = true
                    onReject()
                }) {
                    Text("Reject")
                }
            }
        }
    }
}
