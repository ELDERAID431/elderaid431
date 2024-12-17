package com.example.elderaid.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

data class User(
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val age: String = "",
    val location: String = "",
    val role: String = ""
)

class SignupViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun signup(
        user: User,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(user.email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        firestore.collection("users").document(userId)
                            .set(user)
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { onFailure(it.message ?: "Firestore error") }
                    }
                } else {
                    onFailure(task.exception?.localizedMessage ?: "Signup failed")
                }
            }
    }
}
