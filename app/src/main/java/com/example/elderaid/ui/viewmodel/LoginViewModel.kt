package com.example.elderaid.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {
    fun login(email: String, password: String, onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            onFailure("Email and password cannot be empty")
            return
        }

        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onSuccess()
                else onFailure(task.exception?.localizedMessage ?: "Login failed")
            }
    }
}
