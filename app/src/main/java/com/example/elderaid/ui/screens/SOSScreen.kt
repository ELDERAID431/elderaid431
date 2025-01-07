package com.example.elderaid.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

@Composable
fun SOSScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("SOS Alert", style = MaterialTheme.typography.headlineMedium, color = Color.Red)

        Spacer(modifier = Modifier.height(16.dp))

        // Button to send SOS (call 155)
        Button(
            onClick = {
                val callIntent = Intent(Intent.ACTION_CALL).apply {
                    data = Uri.parse("tel:155")
                }
                // Ensure permission is granted before starting the call
                ContextCompat.startActivity(context, callIntent, null)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
        ) {
            Text("Send SOS", color = Color.White)
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(onClick = onBack) {
            Text("Back")
        }
    }
}