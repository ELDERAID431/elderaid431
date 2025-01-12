package com.example.elderaid.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.FirebaseAuth
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

    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid ?: return@LaunchedEffect
        firestore.collection("help_requests")
            .whereEqualTo("creatorId", userId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                offers = querySnapshot.documents.mapNotNull { it.data }
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
                    OfferCard(
                        offer = offer,
                        onAccept = { /* Add Accept logic */ },
                        onReject = { /* Add Reject logic */ },
                        onDetails = {
                            selectedOffer = offer // Show offer details in a dialog
                        }
                    )
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
