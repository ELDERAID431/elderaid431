package com.example.elderaid.ui.screens

import HelpRequestCard
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ElderMainScreen(
    previousRequests: List<Map<String, Any>>,
    isLoading: Boolean,
    errorMessage: String?,
    onNewRequestClick: () -> Unit,
    onViewApplicantsClick: (String) -> Unit,
    onProfileClick: () -> Unit,
    onSOSClick: () -> Unit,
    onVolunteerOffersClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "My Help Requests",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            errorMessage?.let {
                Text(
                    text = "Error: $it",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }

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
                                requestTitle = request["title"] as? String ?: "No Title",
                                onClick = {
                                    onViewApplicantsClick(request["id"] as? String ?: "")
                                }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onNewRequestClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Create New Help Request")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onVolunteerOffersClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Volunteer Offers")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = onProfileClick,
                    modifier = Modifier
                        .weight(1f)
                        .padding(end = 8.dp)
                ) {
                    Text("Profile")
                }
                Button(
                    onClick = onSOSClick,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    Text("SOS")
                }
            }
        }
    }
}