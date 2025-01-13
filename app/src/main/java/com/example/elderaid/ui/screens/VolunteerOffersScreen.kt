package com.example.elderaid.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.elderaid.R
import com.example.elderaid.ui.components.OfferCard
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun VolunteerOffersScreen(
    onBack: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    var offers by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedVolunteerInfo by remember { mutableStateOf<Map<String, String>?>(null) }
    var username by remember { mutableStateOf("Steven") } // Default username

    // Fetch username
    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid ?: return@LaunchedEffect
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                username = document.getString("fullName") ?: "Unknown"
            }
            .addOnFailureListener {
                println("Error fetching username: ${it.localizedMessage}")
            }
    }

    // Fetch offers with accepted volunteers
    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid ?: return@LaunchedEffect
        firestore.collection("help_requests")
            .whereEqualTo("creatorId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                offers = querySnapshot.documents.mapNotNull { doc ->
                    val data = doc.data
                    val acceptedVolunteers = data?.get("acceptedVolunteers") as? List<*>
                    if (!acceptedVolunteers.isNullOrEmpty()) {
                        data["id"] = doc.id
                        data
                    } else null
                }
                isLoading = false
            }
            .addOnFailureListener { exception ->
                errorMessage = exception.localizedMessage
                isLoading = false
            }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Hello, $username!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Serif
                )
                Text(text = "Have a nice day!", fontSize = 16.sp, color = Color.Gray)
            }
            Image(
                painter = painterResource(id = R.drawable.profile),
                contentDescription = "Profile Icon",
                modifier = Modifier.size(50.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (errorMessage != null) {
            Text(text = "Error: $errorMessage", color = MaterialTheme.colorScheme.error)
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(offers) { offer ->
                    val acceptedVolunteers = offer["acceptedVolunteers"] as? List<*> ?: emptyList<Any>()
                    acceptedVolunteers.forEach { volunteerId ->
                        Column {
                            OfferCard(
                                offer = offer,
                                onAccept = {
                                    // Fetch volunteer details
                                    fetchVolunteerDetails(firestore, volunteerId.toString()) { volunteerInfo ->
                                        selectedVolunteerInfo = volunteerInfo
                                    }
                                },
                                onReject = { /* Handle Reject Logic */ },
                                onDetails = { /* Show details of the offer */ }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Image(
                                painter = painterResource(id = R.drawable.line_8),
                                contentDescription = "Separator Line",
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        // Show volunteer details dialog
        selectedVolunteerInfo?.let { volunteerInfo ->
            VolunteerDetailsDialog(
                volunteerInfo = volunteerInfo,
                onCall = {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:${volunteerInfo["phone"]}")
                    context.startActivity(intent)
                },
                onDismiss = {
                    selectedVolunteerInfo = null
                }
            )
        }

        // Navigation buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.drawable.geri),
                contentDescription = "Back Button",
                modifier = Modifier
                    .size(50.dp)
                    .clickable { onBack() }
            )
            Image(
                painter = painterResource(id = R.drawable.ileri),
                contentDescription = "Next Button",
                modifier = Modifier.size(50.dp)
            )
        }
    }
}

fun fetchVolunteerDetails(
    firestore: FirebaseFirestore,
    volunteerId: String,
    onSuccess: (Map<String, String>) -> Unit
) {
    firestore.collection("users").document(volunteerId).get()
        .addOnSuccessListener { document ->
            val volunteerInfo = mapOf(
                "name" to (document.getString("fullName") ?: "Unknown"),
                "phone" to (document.getString("phoneNumber") ?: "Unknown")
            )
            onSuccess(volunteerInfo)
        }
        .addOnFailureListener {
            println("Error fetching volunteer details: ${it.localizedMessage}")
        }
}

@Composable
fun VolunteerDetailsDialog(
    volunteerInfo: Map<String, String>,
    onCall: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onCall) {
                Text("Call")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        },
        title = {
            Text(text = volunteerInfo["name"] ?: "Unknown")
        },
        text = {
            Text("Phone: ${volunteerInfo["phone"] ?: "Unknown"}")
        }
    )
}
