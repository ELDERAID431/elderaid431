package com.example.elderaid.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun NewHelpRequestScreen(
    onSubmitSuccess: () -> Unit,
    onCancel: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    // State variables
    var date by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("") }
    var endTime by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Text("New Task", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // Date and Location
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text("Date", fontSize = 16.sp)
                TextField(
                    value = date,
                    onValueChange = { date = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                Text("Location", fontSize = 16.sp)
                TextField(
                    value = location,
                    onValueChange = { location = it },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Start and End Time
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text("Start Time", fontSize = 16.sp)
                TextField(
                    value = startTime,
                    onValueChange = { startTime = it },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
            Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                Text("End Time", fontSize = 16.sp)
                TextField(
                    value = endTime,
                    onValueChange = { endTime = it },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text("Description", fontSize = 16.sp)
        TextField(
            value = description,
            onValueChange = { description = it },
            modifier = Modifier.fillMaxWidth().height(80.dp),
            singleLine = false
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Category Section
        Text("Category", fontSize = 16.sp, modifier = Modifier.align(Alignment.Start))
        Spacer(modifier = Modifier.height(8.dp))
        Column {
            val categories = listOf(
                "Home shopping", "Coffee and tea time", "Pharmacy", "House cleaning",
                "Brain exercises", "Chatting", "Walks", "Food provision"
            )
            categories.chunked(2).forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    row.forEach { categoryName ->
                        OutlinedButton(
                            onClick = { category = categoryName },
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                containerColor = if (category == categoryName) Color.Gray else Color.White
                            ),
                            modifier = Modifier.weight(1f).padding(4.dp)
                        ) {
                            Text(categoryName, fontSize = 12.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Buttons (Cancel and Submit)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onCancel, modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                Text("Cancel")
            }
            Button(
                onClick = {
                    val userId = auth.currentUser?.uid
                    if (userId == null) {
                        errorMessage = "User not logged in"
                    } else if (date.isBlank() || location.isBlank() || startTime.isBlank() ||
                        endTime.isBlank() || description.isBlank() || category.isBlank()
                    ) {
                        errorMessage = "All fields are required"
                    } else {
                        isLoading = true
                        errorMessage = null
                        val helpRequest = hashMapOf(
                            "date" to date,
                            "location" to location,
                            "startTime" to startTime,
                            "endTime" to endTime,
                            "description" to description,
                            "category" to category,
                            "createdBy" to userId,
                            "timestamp" to System.currentTimeMillis()
                        )
                        firestore.collection("help_requests")
                            .add(helpRequest)
                            .addOnSuccessListener {
                                isLoading = false
                                onSubmitSuccess()
                            }
                            .addOnFailureListener { exception ->
                                errorMessage =
                                    exception.localizedMessage ?: "Failed to save request"
                                isLoading = false
                            }
                    }
                },
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            ) {
                Text("Submit")
            }
        }

        // Error Message
        errorMessage?.let {
            Spacer(modifier = Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        // Loading State
        if (isLoading) {
            Spacer(modifier = Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}
