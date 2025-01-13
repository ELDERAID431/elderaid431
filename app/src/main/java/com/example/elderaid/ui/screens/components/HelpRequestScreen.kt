package com.example.elderaid.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.elderaid.R

@Composable
fun HelpRequestCard(
    title: String,
    location: String,
    time: String,
    category: String,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sol taraf: Metinler
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = title, fontSize = 16.sp, color = Color.Black)
                Text(text = location, fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = time, fontSize = 14.sp, color = Color.Black)
                Text(text = category, fontSize = 14.sp, color = Color.Gray)
            }

            // Sağ taraf: Butonlar
            Row(
                modifier = Modifier.wrapContentWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onAccept,
                    modifier = Modifier.size(40.dp) // Buton boyutunu arttırdık
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.accept),
                        contentDescription = "Accept",
                        tint = Color.Green
                    )
                }
                IconButton(
                    onClick = onReject,
                    modifier = Modifier.size(40.dp) // Buton boyutunu arttırdık
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.reject),
                        contentDescription = "Reject",
                        tint = Color.Red
                    )
                }
            }
        }
    }
}
