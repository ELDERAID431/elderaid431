package com.example.elderaid.ui.screens

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.elderaid.R

@Composable
fun SOSScreen(
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEAEAEA)), // Light gray background
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp), // Increased padding for better spacing
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceEvenly
        ) {
            // Instruction Texts as Images
            Image(
                painter = painterResource(id = R.drawable.press_hold), // Replace with your drawable for "PRESS AND HOLD"
                contentDescription = "Press and Hold Text",
                modifier = Modifier
                    .height(60.dp) // Increased size for better readability
                    .width(250.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Image(
                painter = painterResource(id = R.drawable.tosend), // Replace with your drawable for "to send SOS alert"
                contentDescription = "To Send SOS Alert Text",
                modifier = Modifier
                    .height(50.dp) // Increased size for better readability
                    .width(250.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // SOS Button
            Box(
                modifier = Modifier
                    .size(200.dp) // Increased outer SOS button size
                    .background(Color.Transparent),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.sos), // Replace with your drawable for SOS button background
                    contentDescription = "SOS Button Background",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                // Circular SOS Button
                androidx.compose.material3.Button(
                    onClick = {
                        if (ContextCompat.checkSelfPermission(
                                context,
                                android.Manifest.permission.CALL_PHONE
                            ) == android.content.pm.PackageManager.PERMISSION_GRANTED
                        ) {
                            val callIntent = Intent(Intent.ACTION_CALL).apply {
                                data = Uri.parse("tel:+901111111111") // Replace with your desired number
                            }
                            ContextCompat.startActivity(context, callIntent, null)
                        } else {
                            ActivityCompat.requestPermissions(
                                context as Activity,
                                arrayOf(android.Manifest.permission.CALL_PHONE),
                                1
                            )
                        }
                    },
                    shape = CircleShape,
                    colors = androidx.compose.material3.ButtonDefaults.buttonColors(containerColor = Color.Red),
                    modifier = Modifier.size(120.dp) // Inner SOS button size increased
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.sos), // Replace with your drawable for SOS text
                        contentDescription = "SOS Text",
                        modifier = Modifier.size(80.dp) // Adjusted text size inside button
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Help Text as Image
            Image(
                painter = painterResource(id = R.drawable.helpme), // Replace with your drawable for "HELP ME"
                contentDescription = "Help Me Text",
                modifier = Modifier
                    .height(50.dp) // Increased size for better readability
                    .width(200.dp)
            )

            // Navigation Button (Back Only)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Image(
                    painter = painterResource(id = R.drawable.geri), // Replace with your drawable for the back button
                    contentDescription = "Back Button",
                    modifier = Modifier
                        .size(70.dp) // Increased button size
                        .clickable { onBack() }
                )
            }
        }
    }
}
