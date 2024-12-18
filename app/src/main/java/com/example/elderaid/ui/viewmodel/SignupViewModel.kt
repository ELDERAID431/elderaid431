package com.example.elderaid.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthUserCollisionException
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
        // Firebase Authentication ile kullanıcı kaydı oluşturma
        auth.createUserWithEmailAndPassword(user.email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid
                    if (userId != null) {
                        // Kullanıcı bilgilerini Firestore'a kaydetme
                        firestore.collection("users").document(userId)
                            .set(user)
                            .addOnSuccessListener { onSuccess() }
                            .addOnFailureListener { onFailure(it.message ?: "Firestore error") }
                    }
                } else {
                    // Eğer hata varsa kontrol et
                    val exception = task.exception
                    if (exception is FirebaseAuthUserCollisionException) {
                        onFailure("Bu email adresi ile hesap bulunmaktadır.")
                    } else {
                        onFailure(exception?.localizedMessage ?: "Kayıt sırasında bir hata oluştu.")
                    }
                }
            }
    }
}
