package com.example.elderaid.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.elderaid.ui.components.OfferCard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun VolunteerMainScreen(
    onTaskClick: (Map<String, Any>) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    var tasks by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var requestSentMessage by remember { mutableStateOf<String?>(null) }

    // Fetch tasks from Firestore
    LaunchedEffect(Unit) {
        firestore.collection("help_requests")
            .get()
            .addOnSuccessListener { querySnapshot ->
                tasks = querySnapshot.documents.mapNotNull { doc ->
                    val data = doc.data
                    data?.put("id", doc.id) // Add the document ID for later use
                    data
                }
                isLoading = false
            }
            .addOnFailureListener { exception ->
                errorMessage = exception.localizedMessage
                isLoading = false
            }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Show "Request Sent" message
        requestSentMessage?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Show loading or error messages
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
                            val volunteerId = auth.currentUser?.uid
                            if (volunteerId != null) {
                                acceptTask(
                                    firestore = firestore,
                                    taskId = task["id"] as String,
                                    volunteerId = volunteerId,
                                    onSuccess = {
                                        requestSentMessage = "Request Sent Successfully!"
                                    },
                                    onFailure = {
                                        requestSentMessage = "Failed to Send Request"
                                    }
                                )
                            } else {
                                requestSentMessage = "User not logged in."
                            }
                        },
                        onReject = {
                            requestSentMessage = "Task Rejected"
                        },
                        onDetails = {
                            onTaskClick(task)
                        }
                    )
                }
            }
        }
    }
}

fun acceptTask(
    firestore: FirebaseFirestore,
    taskId: String,
    volunteerId: String,
    onSuccess: () -> Unit,
    onFailure: (Exception) -> Unit
) {
    val taskRef = firestore.collection("help_requests").document(taskId)
    taskRef.update("acceptedVolunteers", FieldValue.arrayUnion(volunteerId))
        .addOnSuccessListener {
            onSuccess()
        }
        .addOnFailureListener { exception ->
            onFailure(exception)
        }
}
