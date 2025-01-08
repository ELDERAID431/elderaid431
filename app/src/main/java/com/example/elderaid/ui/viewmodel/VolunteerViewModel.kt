package com.example.elderaid.ui.screens

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore

class VolunteerViewModel : ViewModel() {
    val tasks = mutableStateOf<List<Map<String, String>>>(emptyList())
    val isLoading = mutableStateOf(true)
    val errorMessage = mutableStateOf<String?>(null)

    init {
        fetchTasks()
    }

    private fun fetchTasks() {
        isLoading.value = true
        FirebaseFirestore.getInstance().collection("help_requests")
            .get()
            .addOnSuccessListener { documents ->
                val taskList = documents.map { document ->
                    mapOf(
                        "title" to (document.getString("title") ?: "No Title"),
                        "description" to (document.getString("description") ?: "No Description"),
                        "timestamp" to (document.getString("timestamp") ?: "Unknown")
                    )
                }
                tasks.value = taskList
                isLoading.value = false
            }
            .addOnFailureListener { exception ->
                errorMessage.value = exception.localizedMessage ?: "Error fetching tasks"
                isLoading.value = false
            }
    }
}
