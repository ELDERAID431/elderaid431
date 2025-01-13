package com.example.elderaid.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.elderaid.R
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun ElderMainScreen(
    userName: String,
    previousRequests: List<Map<String, Any>>,
    isLoading: Boolean,
    errorMessage: String?,
    onNewRequestClick: () -> Unit,
    onVolunteerOffersClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSOSClick: () -> Unit
) {
    var selectedRequest by remember { mutableStateOf<Map<String, Any>?>(null) }

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
            // Top Greeting and Profile Section
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Hello, $userName!",
                        fontSize = 36.sp,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Have a nice day!",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(
                        painter = painterResource(id = R.drawable.profile),
                        contentDescription = "Edit Profile",
                        modifier = Modifier
                            .size(64.dp)
                            .clickable { onProfileClick() }
                    )
                    Text(
                        text = "Edit Profile",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                }
            }

            // Buttons for Volunteer Offers and Create Request
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = onVolunteerOffersClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD9D9D9))
                ) {
                    Text(text = "Voluntary Offers", color = Color.Black)
                }

                Button(
                    onClick = onNewRequestClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD9D9D9))
                ) {
                    Text(text = "Create a request", color = Color.Black)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Section Title
            Text(
                text = "My Requests",
                fontSize = 28.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // "Looking for a Match..." as an image
            Image(
                painter = painterResource(id = R.drawable.looking),
                contentDescription = "Looking for a Match",
                modifier = Modifier
                    .align(Alignment.Start)
                    .size(150.dp) // Adjusted size to maintain layout
                    .padding(bottom = 8.dp) // Add padding if needed to control spacing
            )


            Spacer(modifier = Modifier.height(8.dp))

            // Request List
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else if (!errorMessage.isNullOrEmpty()) {
                Text(
                    text = "Error: $errorMessage",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            } else if (previousRequests.isEmpty()) {
                Text(
                    text = "No requests yet.",
                    color = Color.Gray,
                    modifier = Modifier.padding(8.dp)
                )
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(previousRequests) { request ->
                        RequestCard(request = request) {
                            selectedRequest = request
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SOS Button
            Button(
                onClick = onSOSClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.White),
                modifier = Modifier
                    .size(80.dp)
                    .align(Alignment.CenterHorizontally)
            ) {
                Text("SOS", fontSize = 16.sp, color = Color.Black)
            }
        }

        // Request Details Dialog
        selectedRequest?.let { request ->
            AlertDialog(
                onDismissRequest = { selectedRequest = null },
                confirmButton = {
                    Button(onClick = { selectedRequest = null }) {
                        Text("Close")
                    }
                },
                title = {
                    Text(text = request["title"] as? String ?: "Request Details")
                },
                text = {
                    Column {
                        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

                        val date = (request["date"] as? Long)?.let {
                            dateFormat.format(Date(it))
                        } ?: "No Date"

                        val startTime = (request["startTime"] as? Long)?.let {
                            dateFormat.format(Date(it))
                        } ?: "No Start Time"

                        val endTime = (request["endTime"] as? Long)?.let {
                            dateFormat.format(Date(it))
                        } ?: "No End Time"

                        Text(text = "Description: ${request["description"] as? String ?: "No Description"}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Category: ${request["category"] as? String ?: "No Category"}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Location: ${request["location"] as? String ?: "No Location"}")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Date: $date")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Start Time: $startTime")
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "End Time: $endTime")
                    }
                }
            )
        }
    }
}

@Composable
fun RequestCard(request: Map<String, Any>, onClick: () -> Unit) {
    val category = request["category"] as? String ?: "No Category"
    val title = request["title"] as? String ?: "No Title"
    val time = (request["startTime"] as? Long)?.let { startTime ->
        (request["endTime"] as? Long)?.let { endTime ->
            val formatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
            "${formatter.format(Date(startTime))} - ${formatter.format(Date(endTime))}"
        }
    } ?: "No Time"
    val status = request["status"] as? String ?: "Expected..."

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(text = category, fontSize = 20.sp, color = Color.Black, fontWeight = FontWeight.Bold)
            Text(text = title, fontSize = 18.sp, color = Color.Gray)
            Text(text = time, fontSize = 18.sp, color = Color.Gray)
        }
        Text(text = status, fontSize = 18.sp, color = Color.Black)
    }
    Spacer(modifier = Modifier.height(8.dp))
    Image(
        painter = painterResource(id = R.drawable.line_8),
        contentDescription = "Divider",
        modifier = Modifier
            .fillMaxWidth()
            .height(3.dp)
    )
}