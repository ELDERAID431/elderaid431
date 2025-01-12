package com.example.elderaid.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.example.elderaid.ui.components.OfferCard

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

    // Fetch offers with accepted volunteers
    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid ?: return@LaunchedEffect
        firestore.collection("help_requests")
            .whereEqualTo("creatorId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                offers = querySnapshot.documents.mapNotNull { doc ->
                    val data = doc.data
                    val acceptedVolunteers = data?.get("acceptedVolunteers") as? List<String>
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

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (errorMessage != null) {
            Text(text = "Error: $errorMessage", color = MaterialTheme.colorScheme.error)
        } else {
            LazyColumn {
                items(offers) { offer ->
                    val acceptedVolunteers = offer["acceptedVolunteers"] as? List<String> ?: emptyList()
                    acceptedVolunteers.forEach { volunteerId ->
                        OfferCard(
                            offer = offer,
                            onAccept = {
                                // Fetch volunteer details
                                fetchVolunteerDetails(firestore, volunteerId) { volunteerInfo ->
                                    selectedVolunteerInfo = volunteerInfo
                                }
                            },
                            onReject = { /* Handle Reject Logic */ },
                            onDetails = { /* Show details of the offer */ }
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

        Button(
            onClick = onBack,
            modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
        ) {
            Text("Back to Main Screen")
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
