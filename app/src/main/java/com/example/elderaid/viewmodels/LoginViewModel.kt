package com.example.elderaid.viewmodels


import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {
    var email = mutableStateOf("")
    var password = mutableStateOf("")
    var errorMessage = mutableStateOf("")
    var isLoading = mutableStateOf(false)

    // Giriş fonksiyonu
    fun loginUser(onSuccess: () -> Unit, onFailure: (String) -> Unit) {
        if (email.value.isNotEmpty() && password.value.isNotEmpty()) {
            isLoading.value = true
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email.value, password.value)
                .addOnCompleteListener { task ->
                    isLoading.value = false
                    if (task.isSuccessful) {
                        onSuccess()
                    } else {
                        onFailure(task.exception?.message ?: "Giriş başarısız")
                    }
                }
        } else {
            errorMessage.value = "Lütfen tüm alanları doldurun."
        }
    }
}
