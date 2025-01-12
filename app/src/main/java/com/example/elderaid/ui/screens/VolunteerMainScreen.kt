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


@Composable
fun VolunteerMainScreen(
    onTaskClick: (Map<String, Any>) -> Unit
) {
    var tasks by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

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
                    VolunteerTaskCard(task = task, onTaskClick = onTaskClick)
                }
            }
        }
    }
}

@Composable
fun VolunteerTaskCard(
    task: Map<String, Any>,
    onTaskClick: (Map<String, Any>) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = task["title"] as? String ?: "No Title")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = task["description"] as? String ?: "No Description")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = { onTaskClick(task) }) {
                    Text("Details")
                }
                Button(onClick = { /* Handle Accept */ }) {
                    Text("Accept")
                }
                Button(onClick = { /* Handle Reject */ }) {
                    Text("Reject")
                }
            }
        }
    }
}
