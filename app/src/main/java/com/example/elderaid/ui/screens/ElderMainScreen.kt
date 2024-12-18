package com.example.elderaid.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ElderMainScreen(
    previousRequests: List<String>, // Replace with actual data model
    onNewRequestClick: () -> Unit,
    onViewApplicantsClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    onSOSClick: () -> Unit
) {
    var selectedRequest by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Header with Profile and SOS Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onProfileClick) {
                    Icon(Icons.Default.Person, contentDescription = "Profile", tint = Color.Black)
                }

                Text(
                    text = "My Requests",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )

                IconButton(
                    onClick = onSOSClick,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.Red)
                        .padding(8.dp)
                ) {
                    Icon(Icons.Default.Warning, contentDescription = "SOS", tint = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Previous Help Requests Section
            Text("Previous Help Requests", fontWeight = FontWeight.SemiBold, fontSize = 18.sp)
            if (previousRequests.isEmpty()) {
                Text("No requests yet.", color = Color.Gray, modifier = Modifier.padding(8.dp))
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(previousRequests) { request ->
                        HelpRequestCard(
                            requestTitle = request,
                            onClick = { selectedRequest = request }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // New Help Request Button
            Button(
                onClick = onNewRequestClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add")
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create New Help Request")
            }

            Spacer(modifier = Modifier.height(8.dp))

            // View Applicants for a Request
            selectedRequest?.let {
                Button(
                    onClick = { onViewApplicantsClick(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text("View Applicants for \"$it\"")
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
            .clickable(onClick = onClick)
            .padding(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = requestTitle,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}
