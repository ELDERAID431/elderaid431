package com.example.elderaid.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OfferCard(
    offer: Map<String, Any>,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onDetails: () -> Unit // Added parameter for viewing details
) {
    // Local state to manage the visibility of the "Accept" button
    var isAcceptVisible by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = offer["title"] as? String ?: "No Title")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = offer["description"] as? String ?: "No Description")

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (isAcceptVisible) {
                    Button(onClick = {
                        onAccept()
                    }) {
                        Text("Accept")
                    }
                }
                Button(onClick = {
                    isAcceptVisible = false // Hide the Accept button
                    onReject()
                }) {
                    Text("Reject")
                }
                Button(onClick = onDetails) {
                    Text("Details")
                }
            }
        }
    }
}



