package com.example.elderaid.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.elderaid.R

@Composable
fun OfferCard(
    offer: Map<String, Any>,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onDetails: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        val title = offer["title"] as? String ?: "No Title"
        val description = offer["description"] as? String ?: "No Description"
        val category = offer["category"] as? String ?: "No Category"

        Text(text = title)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = description)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = category)

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.drawable.accept),
                contentDescription = "Accept",
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onAccept() }
            )
            Image(
                painter = painterResource(id = R.drawable.reject),
                contentDescription = "Reject",
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onReject() }
            )
        }
    }
}
