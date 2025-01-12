package com.example.elderaid.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ElderMainScreen(
    previousRequests: List<Map<String, String>>,
    isLoading: Boolean,
    errorMessage: String?,
    onNewRequestClick: () -> Unit,
    onViewApplicantsClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    onSOSClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Section
            Text(
                text = "My Help Requests",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // Loading State
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            // Error State
            errorMessage?.let {
                Text(
                    text = "Error: $it",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }

            // Help Requests List
            if (!isLoading && errorMessage == null) {
                if (previousRequests.isEmpty()) {
                    Text(
                        text = "No requests yet.",
                        color = Color.Gray,
                        modifier = Modifier.padding(8.dp)
                    )
                } else {
                    LazyColumn(modifier = Modifier.weight(1f)) {
                        items(previousRequests) { request ->
                            HelpRequestCard(
                                requestTitle = request["title"] ?: "No Title",
                                onClick = { onViewApplicantsClick(request["id"] ?: "") }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // New Request Button
            Button(
                onClick = onNewRequestClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Create New Help Request")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Buttons
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onProfileClick,
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Text("Profile")
                }
                Button(
                    onClick = onSOSClick,
                    modifier = Modifier.weight(1f).padding(start = 8.dp)
                ) {
                    Text("SOS")
                }
            }
        }
    }
}

@Composable
fun HelpRequestCard(requestTitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = requestTitle,
                fontSize = 16.sp,
                color = Color.Black
            )
        }
    }
}
