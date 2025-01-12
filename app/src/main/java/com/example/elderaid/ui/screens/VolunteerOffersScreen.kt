package com.example.elderaid.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.ui.Alignment
import com.example.elderaid.ui.components.OfferCard

@Composable
fun VolunteerOffersScreen(
    onBack: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    var offers by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedOffer by remember { mutableStateOf<Map<String, Any>?>(null) } // To display details dialog

    // Fetch offers where volunteers have accepted
    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid ?: return@LaunchedEffect
        firestore.collection("help_requests")
            .whereEqualTo("creatorId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                // Filter help_requests that have `acceptedVolunteers` and include their details
                offers = querySnapshot.documents.mapNotNull { document ->
                    val data = document.data
                    val acceptedVolunteers = data?.get("acceptedVolunteers") as? List<String>
                    if (!acceptedVolunteers.isNullOrEmpty()) {
                        data["id"] = document.id // Add document ID for easier handling
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
                    val offerId = offer["id"] as? String ?: return@items
                    val acceptedVolunteers = offer["acceptedVolunteers"] as? List<String> ?: emptyList()

                    // Display the list of accepted volunteers
                    acceptedVolunteers.forEach { volunteerId ->
                        OfferCard(
                            offer = offer,
                            onAccept = { acceptVolunteerOffer(firestore, offerId, volunteerId) },
                            onReject = { rejectVolunteerOffer(firestore, offerId, volunteerId) },
                            onDetails = { selectedOffer = offer }
                        )
                    }
                }
            }
        }

        Button(
            onClick = onBack,
            modifier = Modifier.padding(top = 16.dp).fillMaxWidth()
        ) {
            Text("Back to Main Screen")
        }
    }

    // Offer Details Dialog
    selectedOffer?.let { offer ->
        OfferDetailsDialog(
            offer = offer,
            onDismiss = { selectedOffer = null }
        )
    }
}

@Composable
fun OfferDetailsDialog(
    offer: Map<String, Any>,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        },
        title = {
            Text(text = offer["title"] as? String ?: "No Title")
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Date: ${offer["date"] ?: "No Date"}")
                Text("Start Time: ${offer["startTime"] ?: "No Start Time"}")
                Text("End Time: ${offer["endTime"] ?: "No End Time"}")
                Text("Location: ${offer["location"] ?: "No Location"}")
                Text("Description: ${offer["description"] ?: "No Description"}")
                Text("Category: ${offer["category"] ?: "No Category"}")
            }
        }
    )
}

fun acceptVolunteerOffer(
    firestore: FirebaseFirestore,
    requestId: String,
    volunteerId: String
) {
    val requestRef = firestore.collection("help_requests").document(requestId)
    requestRef.update("finalAcceptedVolunteer", volunteerId)
        .addOnSuccessListener {
            println("Volunteer accepted successfully.")
        }
        .addOnFailureListener {
            println("Error accepting volunteer: ${it.localizedMessage}")
        }
}

fun rejectVolunteerOffer(
    firestore: FirebaseFirestore,
    requestId: String,
    volunteerId: String
) {
    val requestRef = firestore.collection("help_requests").document(requestId)
    requestRef.update("acceptedVolunteers", FieldValue.arrayRemove(volunteerId))
        .addOnSuccessListener {
            println("Volunteer rejected successfully.")
        }
        .addOnFailureListener {
            println("Error rejecting volunteer: ${it.localizedMessage}")
        }
}
