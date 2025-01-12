package com.example.elderaid.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class ElderMainViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    // Fetch previous help requests created by the user
    fun fetchPreviousRequests(
        elderUserId: String,
        onSuccess: (List<Map<String, String>>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (elderUserId.isBlank()) {
            onFailure("User ID is blank.")
            return
        }

        firestore.collection("help_requests")
            .whereEqualTo("creatorId", elderUserId) // Ensure this matches the Firestore field
            .get()
            .addOnSuccessListener { documents ->
                val requests = documents.map { document ->
                    mapOf(
                        "id" to document.id,
                        "title" to (document.getString("title") ?: "No Title"),
                        "description" to (document.getString("description") ?: "No Description"),
                        "location" to (document.getString("location") ?: "No Location"),
                        "timestamp" to (document.getLong("timestamp")?.toString() ?: "Unknown")
                    )
                }
                onSuccess(requests)
            }
            .addOnFailureListener { exception ->
                onFailure(exception.localizedMessage ?: "Failed to fetch requests.")
            }
    }
}
