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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.elderaid.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun VolunteerOffersScreen(
    onBack: () -> Unit,
    onProfileClick: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    var offers by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedVolunteerInfo by remember { mutableStateOf<Map<String, String>?>(null) }
    val currentUserId = auth.currentUser?.uid

    // Fetch offers with accepted volunteers and resolve volunteer names
    LaunchedEffect(Unit) {
        if (currentUserId != null) {
            firestore.collection("help_requests")
                .whereEqualTo("creatorId", currentUserId)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val fetchedOffers = querySnapshot.documents.mapNotNull { doc ->
                        val data = doc.data
                        val acceptedVolunteers = data?.get("acceptedVolunteers") as? List<String>
                        if (!acceptedVolunteers.isNullOrEmpty()) {
                            data["id"] = doc.id
                            data
                        } else null
                    }

                    fetchedOffers.forEach { offer ->
                        val acceptedVolunteers = offer["acceptedVolunteers"] as? List<String>
                        if (!acceptedVolunteers.isNullOrEmpty()) {
                            val volunteerId = acceptedVolunteers.first()
                            firestore.collection("users").document(volunteerId).get()
                                .addOnSuccessListener { volunteerDoc ->
                                    val volunteerName = volunteerDoc.getString("fullName") ?: "Unknown Volunteer"
                                    offer["volunteerName"] = volunteerName
                                    offers = offers.map { o -> if (o["id"] == offer["id"]) offer else o }
                                }
                        }
                    }

                    offers = fetchedOffers
                    isLoading = false
                }
                .addOnFailureListener { exception ->
                    errorMessage = exception.localizedMessage
                    isLoading = false
                }
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Header with user's name
        var userName by remember { mutableStateOf("User") }
        LaunchedEffect(Unit) {
            firestore.collection("users").document(currentUserId ?: "")
                .get()
                .addOnSuccessListener { document ->
                    userName = document.getString("fullName") ?: "User"
                }
        }

        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "Hello, $userName!", style = MaterialTheme.typography.headlineMedium)
                Text(text = "Have a nice day!", style = MaterialTheme.typography.bodyMedium)
            }
            IconButton(onClick = onProfileClick) {
                Icon(
                    painter = painterResource(id = R.drawable.profile),
                    contentDescription = "Profile",
                    modifier = Modifier.size(48.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Content
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (errorMessage != null) {
            Text(text = "Error: $errorMessage", color = MaterialTheme.colorScheme.error)
        } else {
            LazyColumn {
                items(offers) { offer ->
                    val acceptedVolunteers = offer["acceptedVolunteers"] as? List<String> ?: emptyList()
                    acceptedVolunteers.forEach { volunteerId ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = offer["volunteerName"] as? String ?: "Unknown Volunteer",
                                    fontSize = 16.sp
                                )
                                Text(
                                    text = offer["category"] as? String ?: "No Category",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                            }
                            Row {
                                IconButton(onClick = {
                                    fetchVolunteerDetails(firestore, volunteerId) { volunteerInfo ->
                                        selectedVolunteerInfo = volunteerInfo
                                    }
                                }) {
                                    Image(
                                        painter = painterResource(id = R.drawable.accept),
                                        contentDescription = "Accept",
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(onClick = {
                                    val requestId = offer["id"] as? String
                                    if (requestId != null) {
                                        firestore.collection("help_requests").document(requestId)
                                            .update("acceptedVolunteers", FieldValue.arrayRemove(volunteerId))
                                            .addOnSuccessListener {
                                                offers = offers.filterNot { it["id"] == requestId }
                                            }
                                    }
                                }) {
                                    Image(
                                        painter = painterResource(id = R.drawable.reject),
                                        contentDescription = "Reject",
                                        modifier = Modifier.size(32.dp)
                                    )
                                }
                            }
                        }
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

        // Navigation Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(id = R.drawable.geri),
                contentDescription = "Back",
                modifier = Modifier
                    .size(48.dp)
                    .clickable { onBack() }
            )
            Image(
                painter = painterResource(id = R.drawable.ileri),
                contentDescription = "Forward",
                modifier = Modifier.size(48.dp)
            )
        }
    }
}

// Fetch volunteer details
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

// VolunteerDetailsDialog
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
