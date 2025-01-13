package com.example.elderaid.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.elderaid.R
import com.example.elderaid.ui.components.HelpRequestCard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun VolunteerMainScreen(
    onTaskClick: (Map<String, Any>) -> Unit,
    onProfileClick: () -> Unit,
    onSOSClick: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    var tasks by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var requestSentMessage by remember { mutableStateOf<String?>(null) }

    // Fetch tasks from Firestore and resolve creator names
    LaunchedEffect(Unit) {
        isLoading = true
        firestore.collection("help_requests")
            .get()
            .addOnSuccessListener { querySnapshot ->
                val fetchedTasks = querySnapshot.documents.mapNotNull { doc ->
                    val data = doc.data
                    data?.put("id", doc.id) // Add the document ID for later use
                    data
                }

                // Fetch creator names for each task
                fetchedTasks.forEach { task ->
                    val creatorId = task["creatorId"] as? String
                    if (creatorId != null) {
                        firestore.collection("users").document(creatorId).get()
                            .addOnSuccessListener { userDoc ->
                                val creatorName = userDoc.getString("fullName") ?: "Unknown"
                                task["creatorName"] = creatorName
                                tasks = fetchedTasks // Update tasks after fetching creator names
                            }
                            .addOnFailureListener { exception ->
                                errorMessage = "Failed to fetch creator name: ${exception.localizedMessage}"
                            }
                    }
                }
                isLoading = false
            }
            .addOnFailureListener { exception ->
                errorMessage = exception.localizedMessage
                isLoading = false
            }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Top section with greeting and profile
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Hello, ${auth.currentUser?.displayName ?: "Volunteer"}!",
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(text = "Have a nice day!", color = Color.Gray)
            }
            IconButton(onClick = onProfileClick) {
                Icon(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = "Edit Profile",
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

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
                    val creatorName = task["creatorName"] as? String ?: "Unknown"
                    val location = task["location"] as? String ?: "No Location"
                    val startTime = task["startTime"] as? Long ?: 0L
                    val endTime = task["endTime"] as? Long ?: 0L
                    val category = task["category"] as? String ?: "No Category"

                    val locationShort = location.split(", ").lastOrNull() ?: location
                    val time = "${formatTime(startTime)} - ${formatTime(endTime)}"

                    HelpRequestCard(
                        title = creatorName,
                        location = locationShort,
                        time = time,
                        category = category,
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
                        onClick = { onTaskClick(task) }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SOS Button
        Button(
            onClick = onSOSClick,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White)
        ) {
            Text("SOS", fontSize = 16.sp)
        }
    }
}

// Function to accept a task
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

// Function to format time
fun formatTime(timestamp: Long): String {
    val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
    return formatter.format(Date(timestamp))
}
