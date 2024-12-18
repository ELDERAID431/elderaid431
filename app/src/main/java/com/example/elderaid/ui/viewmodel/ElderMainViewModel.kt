package com.example.elderaid.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class ElderMainViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    // Fetch previous help requests
    fun fetchPreviousRequests(
        elderUserId: String,
        onSuccess: (List<String>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        firestore.collection("help_requests")
            .whereEqualTo("createdBy", elderUserId)
            .get()
            .addOnSuccessListener { documents ->
                val requests = documents.map { it.getString("title") ?: "Unnamed Request" }
                onSuccess(requests)
            }
            .addOnFailureListener { exception ->
                onFailure(exception.localizedMessage ?: "Failed to fetch requests")
            }
    }

    // Create a new help request
    fun createNewRequest(
        elderUserId: String,
        title: String,
        description: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (title.isBlank() || description.isBlank()) {
            onFailure("Title and description cannot be empty")
            return
        }

        val newRequest = hashMapOf(
            "title" to title,
            "description" to description,
            "createdBy" to elderUserId,
            "timestamp" to System.currentTimeMillis()
        )

        firestore.collection("help_requests")
            .add(newRequest)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { exception ->
                onFailure(exception.localizedMessage ?: "Failed to create request")
            }
    }
}
