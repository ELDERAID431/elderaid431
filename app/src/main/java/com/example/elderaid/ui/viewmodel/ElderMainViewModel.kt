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
        firestore.collection("help_requests")
            .whereEqualTo("createdBy", elderUserId) // Filter by the current user's ID
            .get()
            .addOnSuccessListener { documents ->
                val requests = documents.map { document ->
                    mapOf(
                        "id" to document.id,
                        "title" to (document.getString("title") ?: "No Title"),
                        "description" to (document.getString("description") ?: "No Description")
                    )
                }
                onSuccess(requests) // Pass the list of requests to the caller
            }
            .addOnFailureListener { exception ->
                onFailure(exception.localizedMessage ?: "Failed to fetch requests")
            }
    }

    // Create a new help request and save it to Firestore
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
            .addOnSuccessListener {
                onSuccess() // Notify success
            }
            .addOnFailureListener { exception ->
                onFailure(exception.localizedMessage ?: "Failed to create request")
            }
    }

    // Delete a help request
    fun deleteRequest(
        requestId: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        firestore.collection("help_requests")
            .document(requestId)
            .delete()
            .addOnSuccessListener {
                onSuccess() // Notify success
            }
            .addOnFailureListener { exception ->
                onFailure(exception.localizedMessage ?: "Failed to delete request")
            }
    }

    // Update a help request (if needed in the future)
    fun updateRequest(
        requestId: String,
        updatedData: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        firestore.collection("help_requests")
            .document(requestId)
            .update(updatedData)
            .addOnSuccessListener {
                onSuccess() // Notify success
            }
            .addOnFailureListener { exception ->
                onFailure(exception.localizedMessage ?: "Failed to update request")
            }
    }
}