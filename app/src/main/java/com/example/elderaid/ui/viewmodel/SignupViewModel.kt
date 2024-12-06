package com.example.elderaid.ui.viewmodel
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class SignupViewModel : ViewModel() {
    fun signup(
        name: String,
        surname: String,
        age: String,
        gender: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onSuccess() else onFailure(task.exception?.message ?: "Signup failed")
            }
    }
}