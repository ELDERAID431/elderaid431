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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun VolunteerMainScreen(
    onTaskClick: (Map<String, Any>) -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    var tasks by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var userName by remember { mutableStateOf<String?>(null) }
    var isUserLoading by remember { mutableStateOf(true) }

    // Fetch user name from Firebase
    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid
        if (userId != null) {
            firestore.collection("users").document(userId)
                .get()
                .addOnSuccessListener { document ->
                    userName = document.getString("fullName") ?: "User"
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

    // Fetch tasks from Firebase
    LaunchedEffect(Unit) {
        firestore.collection("help_requests")
            .get()
            .addOnSuccessListener { querySnapshot ->
                tasks = querySnapshot.documents.map { it.data ?: emptyMap() }
                isLoading = false
            }
            .addOnFailureListener { exception ->
                errorMessage = "Failed to load tasks: ${exception.localizedMessage}"
                isLoading = false
            }
    }

    // UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (isUserLoading) {
            CircularProgressIndicator()
        } else {
            Text(
                text = "Hello, ${userName ?: "User"}!",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        if (isLoading) {
            CircularProgressIndicator()
        } else if (!errorMessage.isNullOrEmpty()) {
            Text(text = "Error: $errorMessage", color = MaterialTheme.colorScheme.error)
        } else {
            LazyColumn {
                items(tasks) { task ->
                    TaskCard(task, onTaskClick)
                }
            }
        }
    }
}

@Composable
fun TaskCard(task: Map<String, Any>, onTaskClick: (Map<String, Any>) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onTaskClick(task) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = task["title"] as? String ?: "No Title", style = MaterialTheme.typography.bodyLarge)
            Text(
                text = task["description"] as? String ?: "No Description",
                style = MaterialTheme.typography.bodySmall
            )
            val date = task["date"] as? Long
            if (date != null) {
                val formattedDate = SimpleDateFormat("EEEE, dd MMM yyyy", Locale.getDefault()).format(Date(date))
                Text(text = "Date: $formattedDate", style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}
