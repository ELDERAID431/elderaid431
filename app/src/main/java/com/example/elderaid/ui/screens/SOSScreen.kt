package com.example.elderaid.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
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
                // İzin kontrolü
                if (ContextCompat.checkSelfPermission(context, android.Manifest.permission.CALL_PHONE) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                    // İzin verilmişse çağrıyı yap
                    val callIntent = Intent(Intent.ACTION_CALL).apply {
                        data = Uri.parse("tel:+901111111111")
                    }
                    ContextCompat.startActivity(context, callIntent, null)
                } else {
                    // İzin verilmemişse kullanıcıdan izin iste
                    ActivityCompat.requestPermissions(
                        context as Activity,
                        arrayOf(android.Manifest.permission.CALL_PHONE),
                        1 // İsteğe özel bir izin kodu
                    )
                }
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
