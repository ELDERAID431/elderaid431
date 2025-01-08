package com.example.elderaid.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore

class VolunteerViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    fun fetchHelpRequests(
        onSuccess: (List<Map<String, String>>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        firestore.collection("help_requests")
            .get()
            .addOnSuccessListener { documents ->
                val requests = documents.map { document ->
                    val timestamp = document.getTimestamp("timestamp")
                    mapOf(
                        "id" to document.id,
                        "title" to (document.getString("title") ?: "No Title"),
                        "description" to (document.getString("description") ?: "No Description"),
                        "createdBy" to (document.getString("createdBy") ?: "Unknown"),
                        "timestamp" to (timestamp?.toDate()?.toString() ?: "Unknown")
                    )
                }
                onSuccess(requests)
            }
            .addOnFailureListener { exception ->
                onFailure(exception.localizedMessage ?: "Failed to fetch requests")
            }
    }
}
