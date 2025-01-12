package com.example.elderaid.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun OfferCard(
    offer: Map<String, Any>,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
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
                Button(onClick = onAccept) {
                    Text("Accept")
                }
                Button(onClick = onReject) {
                    Text("Reject")
                }
            }
        }
    }
}
