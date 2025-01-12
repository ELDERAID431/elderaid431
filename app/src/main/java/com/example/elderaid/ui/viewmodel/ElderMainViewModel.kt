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
        firestore.collection("help_requests")
            .whereEqualTo("creatorId", elderUserId)
            .get()
            .addOnSuccessListener { documents ->
                val requests = documents.map { document ->
                    document.data.plus("id" to document.id)
                }
                onSuccess(requests)
            }
            .addOnFailureListener { exception ->
                onFailure(exception.localizedMessage ?: "Failed to fetch requests")
            }
    }

    fun fetchOffersForElder(
        elderUserId: String,
        onSuccess: (List<Map<String, Any>>) -> Unit,
        onFailure: (String) -> Unit
    ) {
        firestore.collection("help_requests")
            .whereEqualTo("creatorId", elderUserId)
            .whereArrayContains("volunteers", true)
            .get()
            .addOnSuccessListener { documents ->
                val offers = documents.map { document ->
                    document.data.plus("id" to document.id)
                }
                onSuccess(offers)
            }
            .addOnFailureListener { exception ->
                onFailure(exception.localizedMessage ?: "Failed to fetch offers")
            }
    }
}
