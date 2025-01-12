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
import androidx.compose.ui.graphics.Color



@Composable
fun VolunteerOffersScreen(
    onBack: () -> Unit
) {
    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()

    var offers by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val userId = auth.currentUser?.uid ?: return@LaunchedEffect
        firestore.collection("help_requests")
            .whereEqualTo("creatorId", userId)
            .whereNotEqualTo("volunteers", emptyList<Any>())
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
                        onAccept = { /* Handle Accept */ },
                        onReject = { /* Handle Reject */ }
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

@Composable
fun OfferCard(
    offer: Map<String, Any>,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    // State to track whether the "Accept" button should be disabled
    var isRejected by remember { mutableStateOf(false) }

    Card(modifier = Modifier
        .fillMaxWidth()
        .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = offer["title"] as? String ?: "No Title")
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = offer["description"] as? String ?: "No Description")

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Accept button becomes disabled if `isRejected` is true
                Button(
                    onClick = {
                        onAccept()
                    },
                    enabled = !isRejected, // Disable the button if rejected
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isRejected) Color.Gray else MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Accept")
                }

                Button(
                    onClick = {
                        isRejected = true // Set state to disable the "Accept" button
                        onReject() // Call the reject logic
                    }
                ) {
                    Text("Reject")
                }
            }
        }
    }
}



