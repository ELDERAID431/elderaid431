package com.example.elderaid.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

@Composable
fun VolunteerMainScreen(
    tasks: List<Map<String, String>>,
    isLoading: Boolean,
    errorMessage: String?,
    onTaskClick: (Map<String, String>) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    var userName by remember { mutableStateOf<String?>(null) }
    var isUserLoading by remember { mutableStateOf(true) }
    var selectedTask by remember { mutableStateOf<Map<String, String>?>(null) }

    // Kullanıcı adını Firebase'den çekme
    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        userName = document.getString("fullName") ?: "User"
                    } else {
                        userName = "User"
                    }
                    isUserLoading = false
                }
                .addOnFailureListener {
                    userName = "User"
                    isUserLoading = false
                }
        } else {
            userName = "User"
            isUserLoading = false
        }
    }

    // UI Gösterimi
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isUserLoading) {
            CircularProgressIndicator()
        } else {
            // Kullanıcı Adını Göster
            Text(
                text = "Hello, ${userName ?: "User"}!",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        // Görev Listesi
        if (isLoading) {
            CircularProgressIndicator()
        } else if (!errorMessage.isNullOrEmpty()) {
            Text(text = "Error: $errorMessage", color = MaterialTheme.colorScheme.error)
        } else {
            LazyColumn {
                items(tasks) { task ->
                    TaskCard(task, onTaskClick = { selectedTask = it })
                }
            }
        }

        // Görev Detayı Popup
        selectedTask?.let { task ->
            TaskDetailDialog(task = task, onDismiss = { selectedTask = null })
        }
    }
}

@Composable
fun TaskCard(task: Map<String, String>, onTaskClick: (Map<String, String>) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onTaskClick(task) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = task["title"] ?: "No Title", style = MaterialTheme.typography.bodyLarge)
            Text(text = task["description"] ?: "No Description", style = MaterialTheme.typography.bodySmall)
            Text(text = task["timestamp"] ?: "Unknown Time", style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun TaskDetailDialog(task: Map<String, String>, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        },
        title = {
            Text(text = task["title"] ?: "No Title")
        },
        text = {
            Column {
                Text("Description: ${task["description"] ?: "No Description"}")
                Text("Timestamp: ${task["timestamp"] ?: "Unknown Time"}")
            }
        }
    )
}

