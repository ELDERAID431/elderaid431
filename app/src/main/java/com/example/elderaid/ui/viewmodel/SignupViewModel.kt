package com.example.elderaid.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupViewModel : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    fun signup(
        name: String,
        surname: String,
        gender: String,
        city: String,
        district: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = FirebaseAuth.getInstance().currentUser?.uid
                    val user = mapOf(
                        "name" to name,
                        "surname" to surname,
                        "gender" to gender,
                        "city" to city,
                        "district" to district,
                        "email" to email
                    )
                    if (userId != null) {
                        FirebaseFirestore.getInstance().collection("users").document(userId)
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
