package com.example.elderaid.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class ElderMainViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    fun fetchPreviousRequests(
        elderUserId: String,
        onSuccess: (List<Map<String, Any>>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        if (elderUserId.isBlank()) {
            onFailure("User ID is blank.")
            return
        }

        firestore.collection("help_requests")
            .whereEqualTo("creatorId", elderUserId)
            .get()
            .addOnSuccessListener { documents ->
                val requests = documents.mapNotNull { document ->
                    try {
                        val data = document.data
                        data["id"] = document.id
                        val timestamp = data["timestamp"]
                        data["timestamp"] = when (timestamp) {
                            is com.google.firebase.Timestamp -> timestamp.toDate().time
                            is Long -> timestamp
                            else -> null // Handle unexpected types
                        }
                        data
                    } catch (e: Exception) {
                        null // Skip invalid data
                    }
                }
                onSuccess(requests)
            }
            .addOnFailureListener { exception ->
                onFailure(exception.localizedMessage ?: "Failed to fetch requests.")
            }
    }
}
