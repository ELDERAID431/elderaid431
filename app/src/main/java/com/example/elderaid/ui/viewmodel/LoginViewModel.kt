package com.example.elderaid.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {

    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        // Check if email or password is blank
        if (email.isBlank()) {
            onFailure("Email cannot be empty")
            return
        }
        if (password.isBlank()) {
            onFailure("Password cannot be empty")
            return
        }

        // Firebase Authentication sign-in
        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Check if user email is verified (optional)
                    val user = FirebaseAuth.getInstance().currentUser
                    if (user != null && user.isEmailVerified) {
                        onSuccess()
                    } else {
                        // Notify the user to verify their email
                        onFailure("Please verify your email before logging in")
                    }
                } else {
                    // Handle authentication failure
                    val errorMessage = task.exception?.localizedMessage ?: "Login failed"
                    onFailure(errorMessage)
                }
            }
    }
}
