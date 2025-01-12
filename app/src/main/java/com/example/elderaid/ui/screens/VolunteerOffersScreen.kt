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
                        data["acceptedVolunteers"] = acceptedVolunteers
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
                    OfferCard(
                        offer = offer,
                        onAccept = { /* Handle Accept Logic */ },
                        onReject = { /* Handle Reject Logic */ },
                        onDetails = { /* Show details of the offer */ }
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
}
